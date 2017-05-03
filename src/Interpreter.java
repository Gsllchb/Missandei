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
	final static int defaultLength = 1024;
	
    public static void main(String[] args) {
		if (args.length <= 0 || args.length > 2) {
			System.out.println("Invalid argument");
			return;
		}
		int length = defaultLength;
		if (args.length == 2) {
			length = Integer.valueOf(args[1]);
		}
		
		char[] tape = new char[length];
		int head = length / 2;
		StringReader input = null; 
		Stack<Integer> leftBracket = new Stack<Integer>();
		
		StringBuilder program = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
			while (br.ready()) {
				program.append(br.readLine());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        try (Scanner in = new Scanner(new BufferedReader(new InputStreamReader(System.in)))) {
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
						input = new StringReader(in.nextLine());
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
	
	static char[] resize(final char[] source, final int head) {
		assert head < 0 || head >= source.length;
		final int length = source.length;
		char[] arr = new char[length * 2];
		if (head < 0) {
			System.arraycopy(source, 0, arr, length, length);
		} else {
			System.arraycopy(source, 0, arr, 0, length);
		}
		return arr;
	}
	 
	static int matchedRightBracket(final CharSequence cs, final int begin) {
		int flag = 1;
		int i;
		for (i = begin + 1; flag > 0; ++i) {
			if (cs.charAt(i) == '[') {
				++flag;
				continue;
			}
			if (cs.charAt(i) == ']') {
				--flag;
			}
		}
		return i - 1;
	}
}