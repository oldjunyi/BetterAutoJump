package com.mmyzd.betterautojump.asm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.google.common.collect.ImmutableMap;

public class ASMHelper {

	private static final ImmutableMap<String, String> PRIMITIVE_TABLE = ImmutableMap.<String, String>builder()
			.put("byte", "B").put("char", "C").put("double", "D").put("float", "F").put("int", "I").put("long", "J")
			.put("short", "S").put("boolean", "Z").put("void", "V").build();

	public static String getModifierInfo(int src, int dst) {
		String s = getModifierText(src);
		if (dst != -1 && src != dst) {
			String t = getModifierText(dst);
			if (s.isEmpty())
				s = "default";
			if (t.isEmpty())
				t = "default";
			return " # " + s + " -> " + t;
		} else {
			return s.isEmpty() ? "" : " # " + s;
		}
	}

	public static String getModifierText(int flag) {
		ArrayList<String> tags = new ArrayList<String>();
		if ((flag & Opcodes.ACC_PRIVATE) != 0) {
			tags.add("private");
		} else if ((flag & Opcodes.ACC_PROTECTED) != 0) {
			tags.add("protected");
		} else if ((flag & Opcodes.ACC_PUBLIC) != 0) {
			tags.add("public");
		}
		if ((flag & Opcodes.ACC_STATIC) != 0)
			tags.add("static");
		if ((flag & Opcodes.ACC_FINAL) != 0)
			tags.add("final");
		return StringUtils.join(tags.iterator(), ' ');
	}

	public static int getModifierFlag(String text) {
		int flag = 0;
		String[] tokens = text.split(" ");
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].equals("private") || tokens[i].equals("protected") || tokens[i].equals("public")) {
				flag &= ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED | Opcodes.ACC_PUBLIC);
				if (tokens[i].equals("private"))
					flag |= Opcodes.ACC_PRIVATE;
				if (tokens[i].equals("protected"))
					flag |= Opcodes.ACC_PROTECTED;
				if (tokens[i].equals("public"))
					flag |= Opcodes.ACC_PUBLIC;
			}
			if (tokens[i].equals("static"))
				flag |= Opcodes.ACC_STATIC;
			if (tokens[i].equals("final"))
				flag |= Opcodes.ACC_FINAL;
		}
		return flag;
	}

	public static String getNameFromRaw(String typedName) {
		LinkedList<String> tokens = ASMHelper.getTokens(typedName);
		if (tokens.isEmpty()) {
			return "";
		} else {
			return tokens.poll();
		}
	}

	public static String getTypeFromRaw(String typedName) {
		typedName = typedName.substring(typedName.indexOf(':') + 1);
		LinkedList<String> tokens = ASMHelper.getTokens(typedName);
		if (tokens.isEmpty()) {
			return "";
		}
		StringBuilder ret = new StringBuilder();
		ret.append(tokens.poll());
		if (typedName.contains("(")) {
			ret.append('(');
			while (!tokens.isEmpty()) {
				ret.append(tokens.poll());
				if (!tokens.isEmpty())
					ret.append(", ");
			}
			ret.append(')');
		}
		return ret.toString();
	}

	public static String getIntlFromType(String name) {
		return name.replace('.', '/');
	}

	public static String getDescFromType(String name) {
		if (name.endsWith("[]"))
			return "[" + getDescFromType(name.substring(0, name.length() - 2));
		if (name.endsWith(")")) {
			int pos = name.indexOf('(');
			LinkedList<String> args = getTokens(name.substring(pos + 1, name.length() - 1));
			StringBuilder desc = new StringBuilder();
			desc.append('(');
			Iterator<String> t = args.iterator();
			while (t.hasNext())
				desc.append(getDescFromType(t.next()));
			desc.append(')');
			desc.append(getDescFromType(name.substring(0, pos)));
			return desc.toString();
		}
		String ret = PRIMITIVE_TABLE.get(name);
		if (ret != null)
			return ret;
		return "L" + getIntlFromType(name) + ";";
	}

	public static String getTypeFromIntl(String intl) {
		return Type.getObjectType(intl).getClassName();
	}

	public static String getTypeFromDesc(String desc) {
		Type t = Type.getType(desc);
		if (desc.startsWith("(")) {
			StringBuilder ret = new StringBuilder();
			Type[] ats = t.getArgumentTypes();
			ret.append(t.getReturnType().getClassName());
			ret.append('(');
			for (int i = 0; i < ats.length; i++) {
				if (i != 0)
					ret.append(", ");
				ret.append(ats[i].getClassName());
			}
			ret.append(')');
			return ret.toString();
		} else {
			return t.getClassName();
		}
	}

	public static LinkedList<String> getTokens(String code) {
		LinkedList<String> ret = new LinkedList<String>();
		int n = code.length();
		for (int i = 0; i < n; i++) {
			char c = code.charAt(i);
			if (c == '%') {
				ret.add(code.substring(i, code.length()));
				break;
			} else {
				StringBuilder s = new StringBuilder();
				while (Character.isLetterOrDigit(c) || "$_.<>[]".indexOf(c) != -1) {
					s.append(c);
					if (++i == n)
						break;
					c = code.charAt(i);
				}
				if (s.length() != 0) {
					ret.add(s.toString());
					i--;
				}
			}
		}
		return ret;
	}

}
