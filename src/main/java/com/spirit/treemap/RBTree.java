package com.spirit.treemap;

import com.sun.org.apache.regexp.internal.RE;

import java.util.TreeMap;

/**
 * @author Spirit
 * <p>
 * 红黑树的属性：
 * 1、根节点必须是黑节点
 * 2、节点要么黑要么红
 * 3、不允许存在连续的红节点出现
 * 4、根节点到任意叶子节点路劲上的黑节点数量都相同
 */
public class RBTree<K extends Comparable<K>, V> {

	private static final boolean RED = true;
	private static final boolean BLACK = false;

	private RBNode root;

	public RBNode getRoot() {
		return root;
	}

	public void setRoot(RBNode root) {
		this.root = root;
	}

	static class RBNode<K extends Comparable<K>, V> {

		private RBNode parent;
		private RBNode left;
		private RBNode right;

		private boolean color;
		private K k;
		private V v;

		public RBNode(RBNode parent, RBNode left, RBNode right, boolean color, K k, V v) {
			this.parent = parent;
			this.left = left;
			this.right = right;
			this.color = color;
			this.k = k;
			this.v = v;
		}

		public RBNode(boolean color, K k, V v) {
			this.color = color;
			this.k = k;
			this.v = v;
		}

		public RBNode(RBNode parent, K k, V v) {
			this.parent = parent;
			this.k = k;
			this.v = v;
		}


		public RBNode() {
		}

		public RBNode getParent() {
			return parent;
		}

		public void setParent(RBNode parent) {
			this.parent = parent;
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

		public boolean isColor() {
			return color;
		}

		public void setColor(boolean color) {
			this.color = color;
		}

		public K getK() {
			return k;
		}

		public void setK(K k) {
			this.k = k;
		}

		public V getV() {
			return v;
		}

		public void setV(V v) {
			this.v = v;
		}

		@Override
		public String toString() {
			return "RBNode{" +
					" left=" + left +
					", right=" + right +
					", color=" + color +
					", k=" + k +
					", v=" + v +
					'}';
		}
	}


	private static RBNode parentOf(RBNode node) {
		return node != null ? node.parent : null;
	}

	private static RBNode leftOf(RBNode node) {
		return node != null ? node.left : null;
	}

	private static RBNode rightOf(RBNode node) {
		return node != null ? node.right : null;
	}

	private static boolean colorOf(RBNode node) {
		return node != null ? node.color : BLACK;
	}

	private static void setColor(RBNode node, boolean color) {
		if (node != null) {
			node.setColor(color);
		}
	}

	public V remove(K key) {
		RBNode node = getNode(key);
		if (node == null) {
			return null;
		}

		V value = (V) node.getV();

		deleteNode(node);
		return value;
	}


	public RBNode getNode(K key) {
		RBNode node = this.root;

		if (node == null) {
			return null;
		}

		// 循环判断找到对应的节点并返回
		while (node != null) {
			int cmp = key.compareTo((K) node.getK());
			if (cmp < 0) {
				node = leftOf(node);
			} else if (cmp > 0) {
				node = rightOf(node);
			} else {
				return node;
			}
		}

		return null;
	}


	/**
	 * 删除的三种情况：(先删除在调整)
	 * 1、直接删除叶子节点
	 * 2、删除的节点只有一个子节点，用子节点代替
	 * 3、删除节点有两个子节点，找到前驱或者后继节点来进行替换，从而转为 情况1或者情况2
	 *
	 * @param node
	 * @return V
	 * @author Spirit
	 */
	public void deleteNode(RBNode node) {
		//情况3：存在两个子节点的情况，使用前驱或者后继替换，替换完就转换为情况1或者2了
		if (rightOf(node) != null && leftOf(node) != null) {
			RBNode successor = predecessor(node);
			// 修改位置
			node.k = successor.k;
			node.v = successor.v;
			// 获取successor的引用
			node = successor;
		}

		RBNode replaceNode = node.left != null ? node.left : node.right;

		if (replaceNode != null) {
			// 情况2：存在一个子节点的情况
			replaceNode.parent = node.parent;
			if (node.parent == null) {
				root = replaceNode;
				System.out.println(root);
			} else if (leftOf(parentOf(node)) == node) {
				parentOf(node).left = replaceNode;
			} else {
				parentOf(node).right = replaceNode;
			}
			node.parent = node.left = node.right = null;

			// 替换完成后调整
			if (node.color == BLACK) {
				fixAfterRemove(replaceNode);
			}
		} else if (node.parent == null) {
			root = null;
		} else {
			// 情况1：不存在子节点的情况
			// 先调整
			if (node.color == BLACK) {
				fixAfterRemove(node);
			}

			// 在删除
			if (parentOf(node) != null) {
				if (leftOf(parentOf(node)) == node) {
					parentOf(node).left = null;
				} else {
					parentOf(node).right = null;
				}
			}

			node = null;
		}

	}

	/**
	 * 为了便于理解，这里都是以2-3-4树作为参考目标：
	 * 1、情况一：自己能搞定，替换的节点是红色节点(3节点或4节点)，那么直接将颜色设置为黑色
	 * 2、情况二：自己不能搞定，向兄弟借，兄弟不借，父亲下来帮忙
	 * 3、情况三：自己不能搞定，兄弟也借不了
	 *
	 * @param node
	 * @author Spirit
	 */
	public void fixAfterRemove(RBNode node) {

		while (node != root && node.color == BLACK) {
			if (leftOf(parentOf(node)) == node) {
				// 获取兄弟节点
				RBNode rnode = rightOf(parentOf(node));
				// 如果兄弟节点是红色，那么根据2-3-4树的分布结构来说，此兄弟节点就不是真正的兄弟节点(可自行画图来理解)
				// 所以此时我们需要找到真正的兄弟节点
				if (rnode.color == RED) {
					setColor(parentOf(node), RED);
					setColor(rnode, BLACK);
					leftRotate(parentOf(node));
					rnode = rightOf(parentOf(node));
				}

				if (colorOf(leftOf(rnode)) == BLACK && colorOf(rightOf(rnode)) == BLACK) {
					// 情况三：兄弟节点借不了，简单来说就是兄弟节点没有子节点
					// 做法：将兄弟节点设置为红色，然后递归处理。简单来说就是当前节点不符合，那就使用父结点去调整
					setColor(rnode, RED);
					node = parentOf(node);
				} else {
					// 情况二：兄弟能借，但是不借，父节点下来帮忙
					// 情况二.1：兄弟节点能借的节点不是右节点的情况，那么此时就需要变色右旋转换为右节点进行处理
					if (colorOf(rightOf(rnode)) == BLACK) {
						setColor(leftOf(rnode), BLACK);
						setColor(rnode, RED);
						rightRotate(rnode);
						rnode = rightOf(parentOf(node));
					}
					setColor(rnode, colorOf(parentOf(node)));
					setColor(parentOf(node), BLACK);
					setColor(rightOf(rnode), BLACK);
					leftRotate(parentOf(node));

					node = root;
				}


			} else {

				RBNode lnode = leftOf(parentOf(node));

				// 兄弟节点为红，那么下面两个节点必为黑
				if (colorOf(lnode) == RED) {
					setColor(lnode, BLACK);
					setColor(parentOf(node), RED);
					rightRotate(parentOf(node));
					lnode = leftOf(parentOf(node));
				}
				if (colorOf(leftOf(lnode)) == BLACK && colorOf(rightOf(lnode)) == BLACK) {
					setColor(lnode, RED);
					node = parentOf(node);
				} else {

					if (colorOf(leftOf(lnode)) == BLACK) {
						setColor(lnode, RED);
						setColor(rightOf(lnode), BLACK);
						leftRotate(lnode);
						lnode = leftOf(parentOf(node));
					}

					setColor(lnode, colorOf(parentOf(node)));
					setColor(parentOf(node), BLACK);
					setColor(leftOf(lnode), BLACK);
					rightRotate(parentOf(node));

					node = root;
				}

			}

		}


		//情况1，直接将红节点设置为黑节点
		setColor(node, BLACK);
	}


	/**
	 * 找到当前节点的前驱节点
	 * 前驱节点的定义：
	 * 比当前节点小的最大值
	 *
	 * @param node
	 * @return null
	 * @author Spirit
	 */
	public RBNode predecessor(RBNode node) {

		if (node == null) {
			return null;
		} else if (leftOf(node) != null) {
			RBNode p = leftOf(node);
			while (p.right != null) {
				p = rightOf(p);
			}
			return p;
		} else {
			// 当前节点没有存在左子节点，也就是当前节点是叶子节点，此时就需要通过父结点去找比此节点小的最大值
			/**
			 * 思路：如果当前节点时父结点的左子节点，代表父结点要比当前节点要大，所以不满足情况，
			 *  此时就要往爷爷节点找，如果父节点也是爷爷节点的左节点，继续往上找，直到找到不是左节点为止，
			 *  那么此时的父结点就是前驱节点
			 *  前驱节点：比当前节点小的最大节点
			 *
			 */

			RBNode pNode = parentOf(node);
			RBNode ch = node;
			while (pNode != null && pNode.left == ch) {
				ch = pNode;
				pNode = parentOf(pNode);
			}
			return pNode;
		}

	}

	public RBNode successor(RBNode node) {

		if (node == null) {
			return null;
		} else if (rightOf(node) != null) {
			RBNode r = rightOf(node);
			while (r.left != null) {
				r = leftOf(r);
			}
			return r;
		} else {
			/**
			 *
			 * 思路：当前节点比左节点大，比右节点小
			 * 	如果当前节点是父结点的左节点那么父结点就是后继节点，如果是右节点，那么就往上找，直到找到左节点为止
			 *
			 * 	后继节点：大于当前节点的最小节点
			 *
			 *
			 */
			RBNode p = parentOf(node);
			RBNode ch = node;

			while (p != null && p.right == ch) {
				ch = p;
				p = parentOf(p);
			}
			return p;

		}


	}


	public void put(K k, V v) {

		RBNode t = root;
		if (t == null) {
			root = new RBNode(null, null, null, BLACK, k, v == null ? k : v);
			return;
		}

		if (k == null) {
			throw new NullPointerException();
		}

		// 作为插入节点的父结点
		RBNode parent;

		// 比较大小的值
		int cmp;

		do {
			parent = t;
			cmp = k.compareTo((K) t.getK());
			if (cmp > 0) {
				t = t.right;
			} else if (cmp < 0) {
				t = t.left;
			} else {
				t.setV(v != null ? v : k);
				return;
			}
		} while (t != null);


		RBNode e = new RBNode(parent, k, v != null ? v : k);
		if (cmp > 0) {
			parent.right = e;
		} else {
			parent.left = e;
		}

		fixAfterPut(e);

	}

	/**
	 * 红黑树插入的情况：
	 * 第一类：父亲节点为红，叔叔节点为黑或者为null
	 * 1、左左左（父结点右旋）   				  右右右（父结点左旋）
	 * 2、左左右（父结点先左旋，然后当前节点右旋）   右右左（父结点先右旋，然后当前节点左旋）
	 * 第二类：父亲节点和叔叔节点为红
	 * 1、左左左/右右右型（父亲和叔叔节点变黑，爷爷节点变红）
	 * 2、左左右/右右右型（根据情况先旋转，然后在变色）
	 * <p>
	 * 注：如果当前节点的爷爷节点不是根节点，那么就递归处理。
	 * 因为如果爷爷节点和爷爷节点的父结点冲突了也就相当于做了一次插入操作，所以按照上面的情况处理即可
	 *
	 * @param x 两种情况；左左左
	 *          1、爷爷左节点为红右节点为null或者为黑
	 *          2、爷爷节点的左右节点为红
	 * @author Spirit
	 */
	private void fixAfterPut(RBNode x) {

		setColor(x, RED);

		while (x != null && x != root && parentOf(x).color == RED) {

			// 1、当前节点的父结点是爷爷节点的左节点
			if (parentOf(x) == parentOf(parentOf(x)).left) {

				// 判断是否存在叔叔节点，存在就判断是否为RED节点，按照第二类做处理，否则第一类
				RBNode rightNode = parentOf(parentOf(x)).right;
				if (colorOf(rightNode) == RED) {
					setColor(rightNode, BLACK);
					setColor(parentOf(x), BLACK);
					setColor(parentOf(rightNode), RED);
					// 递归处理，因为可能爷爷节点的父结点也是RED的情况
					x = parentOf(parentOf(x));
				} else {
					// 不存在叔叔节点或者叔叔节点为BLACK
					/**
					 * 	1、左左左：直接右旋，变色
					 * 	2、左左右：先转换为左左左，在进行右旋
					 */
					if (x == parentOf(x).right) {
						// 将父结点作为x目的：因为左旋完父结点就会变成叶子节点(变成当前节点的子节点)
						x = parentOf(x);
						leftRotate(x);
					}
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					rightRotate(parentOf(parentOf(x)));
				}

			} else {
				// 判断叔叔节点的情况
				RBNode leftNode = parentOf(parentOf(x)).left;
				// 如果是红色，代表是第二类
				if (colorOf(leftNode) == RED) {
					setColor(leftNode, BLACK);
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					// 递归处理
					x = parentOf(parentOf(x));
				} else {
					// 1、首先判断是否是右右左的情况，是就进行右旋
					if (parentOf(x).left == x) {
						//2、 进行右旋
						x = parentOf(x);
						rightRotate(parentOf(x));
					}
					// 3、进行左旋变色
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					leftRotate(parentOf(parentOf(x)));
				}

			}

		}


		setColor(root, BLACK);



		/*RBNode pg = x.parent.parent;
		while (pg != null) {
			if (pg.left.color == RED && (pg.right == null || pg.right.color == BLACK)) {

				if (pg.left.right != null && pg.left.right.color == RED) {
					// 将左左右转换为左左左，进行左旋
					leftRotate(x.parent);
					x = x.left;
				}

				// 左左左进行右旋
				rightRotate(pg);
				// 右旋未做颜色的改变，先将右旋后的pg变为红色

				setColor(pg, !pg.color);
				pg.color = !pg.color;
				pg.parent.color = !pg.parent.color;
				pg = x.parent.parent;
			} else if (pg.right.color == RED && (pg.left == null || pg.left.color == BLACK)) {
				if (pg.right.left != null && pg.right.left.color == RED) {
					// 将左左右转换为左左左，进行左旋
					rightRotate(x.parent);
					x = x.right;
				}
				// 右右右进行左旋
				leftRotate(pg);
				// 左旋未做颜色的改变，先将右旋后的pg变为红色
				pg.color = RED;
				pg = x.parent.parent;
			}

		}*/
	}

	public static void main(String[] args) {
		int key = 1;
		int value = 11;
		RBNode root = new RBNode<>(null, null, null, BLACK, key, value);
		root.right = new RBNode<>(root, null, null, BLACK, 2 * key, 2 * value);
		root.left = new RBNode<>(root, null, null, BLACK, key = key + 1, value = value + 1);
		root.left.right = new RBNode<>(root.left, null, null, RED, key = key + 1, value = value + 1);
		RBNode x = root.left.right.left = new RBNode<>(root.left.left, null, null, RED, key + 1, value + 1);
		/*System.out.println(root);
		System.out.println(x);*/
	/*	fixAfterPut(x);
		System.out.println(RBTree.root);*/
		System.out.println(null == "sa");
	}


	private void leftRotate(RBNode p) {
		if (p != null) {
			// 左旋就是将右子节点作为当前节点p的父结点
			RBNode r = p.right;
			// 判断右子节点是否存在左子节点，存在就将其放到当前节点的右子节点处
			if (r.left != null) {
				p.right = r.left;
				r.left.parent = p;
			} else {
				p.right = null;
			}
			// 判断当前节点p在左旋前是否存在父结点，存在就将父结点作为右子节点的父结点，并把右子节点作为当前节点p的父结点
			if (p.parent != null) {
				if (p.parent.left == p) {
					p.parent.left = r;
				} else {
					p.parent.right = r;
				}
			} else {
				root = r;
				root.color = BLACK;
			}
			r.parent = p.parent;
			r.left = p;
			p.parent = r;
		}
	}

	private void rightRotate(RBNode p) {
		if (p != null) {

			RBNode l = p.left;

			if (l.right != null) {
				p.left = l.right;
				l.right.parent = p;
			} else {
				p.left = null;
			}

			if (p.parent == null) {
				root = l;
				root.color = BLACK;
			} else if (p.parent.left == p) {
				p.parent.left = l;
			} else {
				p.parent.right = l;
			}
			l.parent = p.parent;
			l.right = p;
			p.parent = l;
		}
	}


}
