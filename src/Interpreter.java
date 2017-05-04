//Missandei: A simple JAVA interpreter of Brainfuck.
//Copyright (C) 2017  Gsllchb <Gsllchb@icloud.com>
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Scanner;
import java.util.Stack;

final public class Interpreter {
	final static String VERSION = "0.0";

    public static void main(String[] args) {
		if (args.length <= 0 || args.length > 2) {
			introduce();
			return;
		}
		int tapeLength = DEFAULT_TAPE_LENGTH;
		if (args.length == 2) {
			tapeLength = Integer.valueOf(args[1]);
		}
		
		char[] tape = new char[tapeLength];
		int head = tapeLength / 2;
		StringReader input = null; 
		Stack<Integer> leftBracket = new Stack<Integer>();
		
		StringBuilder program = new StringBuilder();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(args[0]))) {
			while (bufferedReader.ready()) {
				program.append(bufferedReader.readLine()).append("\n");
			}
		} catch (IOException e) {
			System.out.print(e);
			System.exit(-1);
		}
		
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(System.in)))) {
			for (int i = 0; i < program.length(); ++i) {
				assert head >= 0 && head < tape.length;
				switch (program.charAt(i)) {
				case '>':
					if (++head >= tape.length) {
						tape = resize(tape, head);
					}
					break;
				case '<':
					if (--head < 0) {
						tape = resize(tape, head);
						head = tape.length / 2 - 1;
					} 
					break;
				case '+':
					++tape[head];
					break;
				case '-':
					--tape[head];
					break;
				case '.':
					System.out.print(tape[head]);
					break;
				case ',':
					if (input == null || !input.ready()) {
						input = new StringReader(scanner.nextLine());
					}
					tape[head] = (char) input.read();
					break;
				case '[':
					if (tape[head] == 0) {
						i = matchedRightBracket(program, i);
					} else {
						leftBracket.push(i);
					}
					break;
				case ']':
					if (tape[head] == 0) {
						leftBracket.pop();
					} else {
						i = leftBracket.peek();
					}
					break;
				}
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
    final static int DEFAULT_TAPE_LENGTH = 1024;
    
    static void introduce() {
    	System.out.print("Missandei " + VERSION +" Copyright (C) 2017 Gsllchb\n"
    			+ "A simple JAVA interpreter of Brainfuck.\n"
    			+ "Usage:\n"
    			+ "\tjava [-options] -jar Missandei.jar scriptFile [initialTapeLength]\n"
    			+ "More information:\n"
    			+ "\thttps://github.com/Gsllchb/Missandei");
    }
    
	static char[] resize(final char[] source, final int head) {
		assert head < 0 || head >= source.length;
		final int length = source.length;
		char[] array = new char[length * 2];
		if (head < 0) {
			System.arraycopy(source, 0, array, length, length);
		} else {
			System.arraycopy(source, 0, array, 0, length);
		}
		return array;
	}
	 
	static int matchedRightBracket(final CharSequence charSequence, final int leftBracketIndex) {
		int flag = 1;
		int i;
		for (i = leftBracketIndex + 1; flag > 0; ++i) {
			if (charSequence.charAt(i) == '[') {
				++flag;
				continue;
			}
			if (charSequence.charAt(i) == ']') {
				--flag;
			}
		}
		return i - 1;
	}
}