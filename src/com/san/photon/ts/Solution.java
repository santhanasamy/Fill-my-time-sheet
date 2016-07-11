package com.san.photon.ts;

import java.util.Arrays;

public class Solution {
	public static String myFunction(String arg) {

		int len = arg.length() / 2 + 1;

		int ALPHA_COUNT = 26;
		int D_ALPHA_COUNT = ALPHA_COUNT * 2;
		int SMALL_ALPHA_START = 65;
		int CAP_ALPHA_START = 97;

		int[][] lList = new int[D_ALPHA_COUNT][1];

		int tt = arg.charAt(0);
		for (int i = 0; i < arg.length(); i++) {

			tt = arg.charAt(i);
			System.out.println("[" + i + "][" + tt + "]");

			tt = (tt >= CAP_ALPHA_START) ? tt - CAP_ALPHA_START + ALPHA_COUNT : tt - SMALL_ALPHA_START;
			lList[tt][0] = ++lList[tt][0];
		}

		boolean addFound = false, isOdd = false;

		for (int i = 0; i < lList.length; i++) {

			for (int j = 0; j < lList[0].length; j++) {

				isOdd = lList[i][j] % 2 != 0;

				if (isOdd && !addFound) {
					addFound = true;
				}

				if (isOdd && addFound) {
					System.out.println("[ No polindrom ]");
					break;
				}
			}
		}
		return "running with " + Arrays.toString(lList);
	}

	public static void main(String[] args) {
		// run your function through some test cases here
		// remember: debugging is half the battle!
		String testInput = "mom";
		System.out.println(myFunction(testInput));
	}
}
