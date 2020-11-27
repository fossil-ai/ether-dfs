package utils;

public class CommandParser {

	/*
	 * 'Unix'-like Operations - ls 1, {show files in current working directory} - cd
	 * [directory] 2, {navigate to directory} - cat [filename], 2, {read a file} -
	 * nano [filename] 2, {for both creating and writing/editing a file} - pwd 1,
	 * {show the current working directory} - mkdir [dirname], 2 {create directory}
	 * - rm [filename/dir], 2 {delete filename/dir}
	 */

	enum Command {

		LS(1), CD(2), CAT(2), NANO(2), PWD(1), MKDIR(2), RM(2);

		private int argsNum;

		Command(int argsNum) {
			this.argsNum = argsNum;
		}

		public int argsNum() {
			return argsNum;
		}

		public boolean isValid(String[] cmds) {
			if (cmds.length == this.argsNum())
				return true;
			else
				return false;
		}

	}

	public static String parse(String[] cmds) {
		try {
			Command cmd = Command.valueOf(cmds[0].toUpperCase());
			if (cmd.isValid(cmds))
				return cmd.toString();
			else
				return "Operation missing required number of arguments.";
		} catch (IllegalArgumentException e) {
			return "Operation does not exist!";
		}
	}

}
