package com.spirit.treemap1;

/**
 * @author Spirit
 */
public class RBNode<K extends Comparable<K>, V> {

	private K key;
	private V value;
	private RBNode left;
	private RBNode right;
	private RBNode parent;
	private boolean color;


	public RBNode(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public RBNode(K key, V value, RBNode parent) {
		this.key = key;
		this.value = value;
		this.parent = parent;
	}

	public RBNode(K key, V value, RBNode parent, boolean color) {
		this.key = key;
		this.value = value;
		this.parent = parent;
		this.color = color;
	}

	public RBNode(K key, V value, RBNode left, RBNode right, RBNode parent) {
		this.key = key;
		this.value = value;
		this.left = left;
		this.right = right;
		this.parent = parent;
	}

	public RBNode(K key, V value, RBNode left, RBNode right, RBNode parent, boolean color) {
		this.key = key;
		this.value = value;
		this.left = left;
		this.right = right;
		this.parent = parent;
		this.color = color;
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public RBNode getLeft() {
		return left;
	}

	public void setLeft(RBNode left) {
		this.left = left;
	}

	public RBNode getRight() {
		return right;
	}

	public void setRight(RBNode right) {
		this.right = right;
	}

	public RBNode getParent() {
		return parent;
	}

	public void setParent(RBNode parent) {
		this.parent = parent;
	}

	public boolean isColor() {
		return color;
	}

	public void setColor(boolean color) {
		this.color = color;
	}
}
