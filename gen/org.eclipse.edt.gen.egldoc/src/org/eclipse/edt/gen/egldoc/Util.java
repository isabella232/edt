package org.eclipse.edt.gen.egldoc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java_cup.runtime.Symbol;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultLineTracker;

public class Util {

	public static Integer getLine(Element element) {
		Annotation location = element.getAnnotation(IEGLConstants.EGL_LOCATION);
		return (Integer) location.getValue(IEGLConstants.EGL_PARTLINE);
	}

	@SuppressWarnings("unchecked")
	public static String findBlockComment(Context ctx, Integer endingOnLine) {
		String result = null;
		List<Symbol> blockComments = (List<Symbol>) ctx.get("blockComments");
		DefaultLineTracker tracker = (DefaultLineTracker) ctx
				.get("lineTracker");
		String fileContents = (String) ctx.get("fileContents");
		for (Iterator<Symbol> it = blockComments.iterator(); it.hasNext();) {
			Symbol blockComment = (Symbol) (it.next());
			int commentEndLine;
			try {
				commentEndLine = tracker
						.getLineNumberOfOffset(blockComment.right);
				if ((endingOnLine - 2) == commentEndLine) {
					result = fileContents.substring(blockComment.left,
							blockComment.right);
				}
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	public static Map<String, String> parseCommentBlock(String commentBlock) {
		String[] linesFromCommentBlock = extractLinesFromCommentBlock(commentBlock);

		String[] sectionsFromCommentBlock = extractSectionsFromCommentBlock(linesFromCommentBlock);

		Map<String, String> result = buildCommentMap(sectionsFromCommentBlock);

		return result;

	}

	private static Map<String, String> buildCommentMap(
			String[] sectionsFromCommentBlock) {
		Map<String, String> commentMap = new LinkedHashMap<String, String>();

		boolean foundFirstPara = false;
		boolean foundEndOfDescription = false;
		StringBuffer postFirstPara = new StringBuffer();
		for (int i = 0; i < sectionsFromCommentBlock.length; i++) {
			String section = sectionsFromCommentBlock[i];
			if (section.startsWith("@")) {
				// We are no longer looking for a description
				foundEndOfDescription = true;
				commentMap.put("postFirstPara", postFirstPara.toString());

				// We may have a tag
				if (section.length() > 1) {
					String tagName = section.substring(1, section.indexOf(" "))
							.trim();
					if (tagName.length() > 1) {
						// We have a valid tag
						commentMap.put(
								tagName,
								section.substring(section.indexOf(" "),
										section.length()).trim());
					}
				}
			} else if (!foundFirstPara) {
				commentMap.put("firstPara", section);
				foundFirstPara = true;
			} else if (!foundEndOfDescription) {
				if (postFirstPara.length() > 0) {
					postFirstPara.append(System.getProperty("line.separator"));
				}
				postFirstPara.append(section);
			}
		}
		return commentMap;
	}

	private static String[] extractSectionsFromCommentBlock(
			String[] linesFromCommentBlock) {
		List<String> sections = new ArrayList<String>();

		boolean foundFirstNonEmptyLine = false;
		StringBuffer sectionText = new StringBuffer();
		for (int i = 0; i < linesFromCommentBlock.length; i++) {
			String line = linesFromCommentBlock[i].trim();

			if (line.length() == 0) {
				// We are looking for the first non-empty line
				if (foundFirstNonEmptyLine) {
					// We have found the end of a section
					if (sectionText.length() > 0) {
						sections.add(sectionText.toString());
						sectionText = new StringBuffer();
					}
				}
			} else {
				foundFirstNonEmptyLine = true;
				if (line.startsWith("@")) {
					// We are starting a new tag
					if (sectionText.length() > 0) {
						sections.add(sectionText.toString());
						sectionText = new StringBuffer();
					}
				}
				if (sectionText.length() > 0) {
					sectionText.append(" ");
				}
				sectionText.append(line);
			}
		}

		return (String[]) sections.toArray(new String[sections.size()]);
	}

	private static String[] extractLinesFromCommentBlock(String commentBlock) {
		String lines[] = commentBlock.split("[\r?\n|\r]+"); // split into lines
		for (int i = 0; i < lines.length; i++) {
			if (lines.length > 0) {
				if (i == 0) {
					// This is the first line in the comment block - remove the
					// leading /**, any additional *'s, and any white space
					lines[i] = lines[i].replaceAll("^[/][*]+\\s*", "");
				} else if (i + 1 == lines.length) {
					// Last Line - Remove the trailing **/
					lines[i] = lines[i].replaceAll("[*][/]$", "");
				} else {
					// All other lines - Remove the leading * if it exists
					lines[i] = lines[i].replaceAll("^\\s*[*]?", "");
				}
			} else {
				// This block is a single line
				// TODO
			}
		}
		return lines;
	}

	public static String createRelativePath(String packageName) {
		StringBuffer result = new StringBuffer();
		String[] split = packageName.split(".");
		for (int i = 0; i < split.length; i++) {
			result.append("../");
		}
		return result.toString();
	}

	public static String getEGLSimpleType(String typeSignature) {

		int lastPeriod;

		// assume that no one wants the full package name displayed
		// if (typeSignature.startsWith("egl")) {

		lastPeriod = typeSignature.lastIndexOf('.');

		if (lastPeriod != 0) {
			typeSignature = typeSignature.substring(lastPeriod + 1);
			if (typeSignature.endsWith(">")) {
				typeSignature.replace(">", "[]");
			}
		}

		return typeSignature;
	}
}
