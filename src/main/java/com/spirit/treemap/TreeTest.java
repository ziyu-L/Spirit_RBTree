package com.spirit.treemap;

import java.util.Scanner;

/**
 * @author Spirit
 */
public class TreeTest {

	public static void main(String[] args) {
		insertOpt();
	}

	public static void insertOpt() {

		Scanner sc = new Scanner(System.in);
		RBTree rbt = new RBTree();
		boolean flag = true;
		while (flag) {

			System.out.println("输出你需要插入的数:");
			String key = sc.next();

			if (key.length() == 1) {
				key = "00" + key;
			} else if (key.length() == 2) {
				key = "0" + key;
			}
			rbt.put(key, null);
			TreeOperation.show(rbt.getRoot());

			if (Integer.valueOf(key) == 100) {
				flag = false;
			}
		}

		while (true) {
			System.out.println("输出你需要删除的数:");
			String key = sc.next();

			if (key.length() == 1) {
				key = "00" + key;
			} else if (key.length() == 2) {
				key = "0" + key;
			}
			rbt.remove(key);
			System.out.println(rbt);
			TreeOperation.show(rbt.getRoot());
		}

	}

}
