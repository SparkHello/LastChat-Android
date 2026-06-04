package com.lastchat.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun MarkdownRenderer(content: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        parseMarkdown(content).forEach { element ->
            when (element) {
                is MarkdownElement.Text -> RenderInlineMarkdown(element.content)
                is MarkdownElement.CodeBlock -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    CodeBlock(code = element.code, language = element.language)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                is MarkdownElement.Heading -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    val style = when (element.level) {
                        1 -> MaterialTheme.typography.headlineLarge
                        2 -> MaterialTheme.typography.headlineMedium
                        3 -> MaterialTheme.typography.headlineSmall
                        4 -> MaterialTheme.typography.titleLarge
                        5 -> MaterialTheme.typography.titleMedium
                        else -> MaterialTheme.typography.titleSmall
                    }
                    Text(
                        text = element.text,
                        style = style,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                is MarkdownElement.Quote -> {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = element.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
                is MarkdownElement.Divider -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                }
                is MarkdownElement.ListItem -> {
                    Text(
                        text = "${element.bullet} ${element.text}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                is MarkdownElement.Spacer -> {
                    Spacer(modifier = Modifier.height(element.height.dp))
                }
            }
        }
    }
}

@Composable
private fun RenderInlineMarkdown(text: String) {
    val annotated = parseInlineMarkdown(text)
    Text(
        text = annotated,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.fillMaxWidth()
    )
}

private fun parseInlineMarkdown(text: String): AnnotatedString {
    return buildAnnotatedString {
        var remaining = text
        while (remaining.isNotEmpty()) {
            when {
                // Bold **text**
                remaining.startsWith("**") -> {
                    val end = remaining.indexOf("**", 2)
                    if (end > 0) {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(remaining.substring(2, end))
                        }
                        remaining = remaining.substring(end + 2)
                    } else {
                        append("*")
                        remaining = remaining.drop(1)
                    }
                }
                // Italic *text*
                remaining.startsWith("*") -> {
                    val end = remaining.indexOf("*", 1)
                    if (end > 0) {
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(remaining.substring(1, end))
                        }
                        remaining = remaining.substring(end + 1)
                    } else {
                        append("*")
                        remaining = remaining.drop(1)
                    }
                }
                // Inline code `code`
                remaining.startsWith("`") -> {
                    val end = remaining.indexOf("`", 1)
                    if (end > 0) {
                        withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
                            append(remaining.substring(1, end))
                        }
                        remaining = remaining.substring(end + 1)
                    } else {
                        append("`")
                        remaining = remaining.drop(1)
                    }
                }
                // Strikethrough ~~text~~
                remaining.startsWith("~~") -> {
                    val end = remaining.indexOf("~~", 2)
                    if (end > 0) {
                        withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                            append(remaining.substring(2, end))
                        }
                        remaining = remaining.substring(end + 2)
                    } else {
                        append("~")
                        remaining = remaining.drop(1)
                    }
                }
                else -> {
                    val nextSpecial = remaining.indexOfFirst { it == '*' || it == '`' || it == '~' }
                    if (nextSpecial >= 0) {
                        append(remaining.substring(0, nextSpecial))
                        remaining = remaining.substring(nextSpecial)
                    } else {
                        append(remaining)
                        remaining = ""
                    }
                }
            }
        }
    }
}

private sealed class MarkdownElement {
    data class Text(val content: String) : MarkdownElement()
    data class CodeBlock(val code: String, val language: String?) : MarkdownElement()
    data class Heading(val level: Int, val text: String) : MarkdownElement()
    data class Quote(val text: String) : MarkdownElement()
    object Divider : MarkdownElement()
    data class ListItem(val bullet: String, val text: String) : MarkdownElement()
    data class Spacer(val height: Int = 8) : MarkdownElement()
}

private fun parseMarkdown(content: String): List<MarkdownElement> {
    val elements = mutableListOf<MarkdownElement>()
    val lines = content.lines()
    var i = 0

    while (i < lines.size) {
        val line = lines[i]
        when {
            // Fenced code block ```
            line.trimStart().startsWith("```") -> {
                val language = line.trimStart().removePrefix("```").trim().ifBlank { null }
                val codeLines = mutableListOf<String>()
                i++
                while (i < lines.size && !lines[i].trimStart().startsWith("```")) {
                    codeLines.add(lines[i])
                    i++
                }
                elements.add(MarkdownElement.CodeBlock(codeLines.joinToString("\n"), language))
                i++
            }
            // Heading
            line.startsWith("#") -> {
                val level = line.takeWhile { it == '#' }.length.coerceAtMost(6)
                val text = line.drop(level).trim()
                elements.add(MarkdownElement.Heading(level, text))
                i++
            }
            // Quote
            line.trimStart().startsWith(">") -> {
                val text = line.trimStart().removePrefix(">").trim()
                elements.add(MarkdownElement.Quote(text))
                i++
            }
            // Divider
            line.trim() == "---" || line.trim() == "***" || line.trim() == "___" -> {
                elements.add(MarkdownElement.Divider)
                i++
            }
            // List item
            line.trimStart().matches(Regex("^[-*+\\d+\\.]\\s.*")) -> {
                val trimmed = line.trimStart()
                val bullet = if (trimmed.matches(Regex("^\\d+\\..*"))) {
                    trimmed.substringBefore(".") + "."
                } else {
                    "\u2022"
                }
                val text = trimmed.replaceFirst(Regex("^[-*+\\d+\\.]\\s*"), "")
                elements.add(MarkdownElement.ListItem(bullet, text))
                i++
            }
            // Empty line
            line.isBlank() -> {
                elements.add(MarkdownElement.Spacer())
                i++
            }
            // Regular text
            else -> {
                val textLines = mutableListOf<String>()
                while (i < lines.size && lines[i].isNotBlank()
                    && !lines[i].trimStart().startsWith("```")
                    && !lines[i].startsWith("#")
                    && !lines[i].trimStart().startsWith(">")
                    && !lines[i].trim().matches(Regex("^[-*+\\d+\\.]\\s.*"))
                    && lines[i].trim() != "---"
                ) {
                    textLines.add(lines[i])
                    i++
                }
                elements.add(MarkdownElement.Text(textLines.joinToString("\n")))
            }
        }
    }
    return elements
}
