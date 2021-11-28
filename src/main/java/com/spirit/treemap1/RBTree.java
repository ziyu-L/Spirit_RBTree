package com.spirit.treemap1;

/**
 * 红黑树的实现：
 * 1、红黑树的遍历：先序、中序和后序遍历
 * 2、红黑树的插入
 * 3、红黑树的删除
 *
 * @author Spirit
 */
public class RBTree<K extends Comparable<K>, V> {

	private final boolean RED = true;
	private final boolean BLACK = false;
	private RBNode root;

	public RBNode getRoot() {
		return root;
	}

	public boolean colorOf(RBNode node) {
		return node == null ? BLACK : node.isColor();
	}

	public RBNode leftOf(RBNode node) {
		return node.getLeft() == null ? null : node.getLeft();
	}

	public RBNode rightOf(RBNode node) {
		return node.getRight() == null ? null : node.getRight();
	}

	public RBNode parentOf(RBNode node) {
		return node.getParent() == null ? null : node.getParent();
	}

	public void setColor(RBNode node, boolean color) {
		if (node != null) {
			node.setColor(color);
		}
	}

	// 找到对应的前驱节点(比当前值小的最大值)
	public RBNode predecessor(RBNode x) {
		if (x != null && (x = leftOf(x)) != null) {
			while (rightOf(x) != null) {
				x = rightOf(x);
			}
		} else if (x == null) {
			return null;
		} else {
			//  当前节点没有存在左子节点，此时就需要通过父结点去找比此节点小的最大值，当然红黑树是不会出现这种情况，因为会平衡黑节点
			while (x != null && leftOf(parentOf(x)) == x) {
				x = parentOf(x);
			}
			x = parentOf(x);
		}
		return x;
	}

	// 找到对应的后继节点(比当前值大的最小值)
	public RBNode successor(RBNode x) {

		if (x == null) {
			return null;
		} else if (rightOf(x) != null) {
			x = rightOf(x);
			while (leftOf(x) != null) {
				x = leftOf(x);
			}
		} else {
			while (x != null && rightOf(parentOf(x)) == x) {
				x = parentOf(x);
			}
			x = parentOf(x);
		}
		return x;
	}

	// 删除方法：通过前驱或者后继节点来替换目标节点，再根据节点分布情况进行平衡
	public V remove(K key) {
		RBNode node = getNode(key);
		if (node == null) {
			return null;
		}

		deleteNode(node);
		V value = (V) node.getValue();

		return value;
	}

	/**
	 * 删除的情况：
	 * 1、直接删除叶子节点
	 * 2、删除的节点只有一个子节点，用子节点代替
	 * 3、删除节点有两个子节点，找到前驱或者后继节点来进行替换，从而转为 情况1或者情况2
	 *
	 * @param x
	 * @author Spirit
	 */
	private void deleteNode(RBNode x) {
		// 情况3：存在两个节点，使用后继节点进行替换，转换为情况1或者情况2
		if (leftOf(x) != null && rightOf(x) != null) {
			RBNode succNode = predecessor(x);
			x.setKey(succNode.getKey());
			x.setValue(succNode.getValue());
			x = succNode;
		}

		//情况2：存在一个子节点，要么左节点 要么右节点
		RBNode replaceNode = leftOf(x) != null ? leftOf(x) : rightOf(x);

		if (replaceNode != null) {
			// 存在一个子节点情况的节点调整
			replaceNode.setParent(parentOf(x));
			if (parentOf(x) == null) {
				this.root = replaceNode;
			} else if (leftOf(parentOf(x)) == replaceNode) {
				parentOf(x).setLeft(replaceNode);
			} else if (rightOf(parentOf(x)) == replaceNode) {
				parentOf(x).setRight(replaceNode);
			}
			x.setRight(null);
			x.setParent(null);
			x.setLeft(null);

			if (colorOf(x) == BLACK) {
				AfterFixRemove(replaceNode);
			}

		} else if (parentOf(x) == null) {
			root = null;
		} else {

			// 情况1：不存在子节点，如果是黑则调整，不是黑，代表删除节点是红色节点，删除后不会影响黑节点的平衡
			if (colorOf(x) == BLACK) {
				AfterFixRemove(x);
			}

			if (parentOf(x) != null) {
				if (leftOf(parentOf(x)) == x) {
					parentOf(x).setLeft(null);
				} else {
					parentOf(x).setRight(null);
				}
			}

			x = null;

		}

	}

	/**
	 * 为了便于理解，这里时通过2-3-4的类比进行操作的：
	 * 1、删除节点是红色节点，删除后不会影响黑节点的平衡(在deleteNode()中体现)
	 * 2、删除节点是黑色节点，存在红色的子节点，直接使用红色子节点替换
	 * 3、删除节点是黑节点，此时判断兄弟节点是否能够借，能借，父亲节点帮忙
	 * 4、删除节点是黑节点，兄弟节点不能借，此时将兄弟节点置为红，将父亲节点作为待处理的节点，循环处理直至黑色平衡
	 *
	 * @param x
	 * @author Spirit
	 */
	public void AfterFixRemove(RBNode x) {
		while (x != root && colorOf(x) == BLACK) {
			// 判断节点是在父结点的左边还是右边
			if (leftOf(parentOf(x)) == x) {

				// 获取兄弟节点
				RBNode rNode = rightOf(parentOf(x));

				// 根据2-3-4树的特性，叶子节点的兄弟节点也会为黑，所以此节点不是直接的兄弟节点，需要进行左旋
				if (colorOf(rNode) == RED) {
					setColor(parentOf(x), RED);
					setColor(rNode, BLACK);
					leftRotate(parentOf(x));
					rNode = rightOf(parentOf(x));
				}

				if (colorOf(rightOf(rNode)) == BLACK && colorOf(leftOf(rNode)) == BLACK) {
					// 情况四：兄弟节点借不了，简单来说就是兄弟节点没有子节点
					// 做法：将兄弟节点设置为红色，然后递归处理。简单来说就是当前节点不符合，那就使用父结点去调整
					setColor(rNode, RED);
					x = parentOf(x);
				} else {

					// 情况三：删除节点是黑节点，兄弟节点存在自节点，能借，父亲节点帮忙
					// 情况三.1：兄弟节点能借的节点不是右节点的情况，此时就需要变色右旋转换为右节点进行处理
					if (colorOf(rightOf(rNode)) == BLACK) {
						setColor(leftOf(rNode), colorOf(rNode));
						setColor(rNode, RED);
						rightRotate(rNode);
						rNode = rightOf(parentOf(x));
					}

					setColor(rNode, colorOf(parentOf(x)));
					setColor(rightOf(rNode), BLACK);
					setColor(parentOf(x), BLACK);
					leftRotate(parentOf(x));
					x = root;
				}
			} else {

				// 获取兄弟节点
				RBNode lNode = leftOf(parentOf(x));

				// 判断当前兄弟节点是否是真正的兄弟节点，因为当前节点为黑色，根据2-3-4的特性，叶子节点的兄弟节点也会是黑色
				if (colorOf(lNode) == RED) {
					setColor(lNode, colorOf(parentOf(x)));
					setColor(parentOf(x), RED);
					rightRotate(parentOf(x));
					lNode = leftOf(parentOf(x));
				}

				if (colorOf(rightOf(lNode)) == BLACK && colorOf(leftOf(lNode)) == BLACK) {
					setColor(lNode, RED);
					x = parentOf(x);
				} else {

					// 情况三：删除节点是黑节点，兄弟节点存在自节点，能借，父亲节点帮忙
					// 情况三.1：兄弟节点能借的节点不是左节点的情况，此时就需要变色左旋转换为左节点进行处理
					if (colorOf(leftOf(lNode)) == BLACK) {
						setColor(rightOf(lNode), colorOf(lNode));
						setColor(lNode, RED);
						leftRotate(lNode);
						lNode = leftOf(parentOf(x));
					}

					setColor(lNode, colorOf(parentOf(x)));
					setColor(parentOf(x), BLACK);
					setColor(leftOf(lNode), BLACK);
					rightRotate(parentOf(x));

					x = root;
				}

			}
		}

		// 情况二：删除节点是黑色节点，存在红色的子节点，直接使用红色子节点替换
		setColor(x, BLACK);
	}


	private RBNode getNode(K key) {

		RBNode node = this.root;
		while (node != null) {
			int cmp = key.compareTo((K) node.getKey());
			if (cmp > 0) {
				node = rightOf(node);
			} else if (cmp < 0) {
				node = leftOf(node);
			} else {
				return node;
			}
		}
		return null;
	}


	public void put(K key, V value) {

		// key和value的非空判断 ...略

		RBNode node = this.root;

		if (node == null) {
			root = node = new RBNode(key, value == null ? key : value);
			root.setColor(BLACK);
		}

		RBNode parent;
		int cmp;
		do {
			cmp = key.compareTo((K) node.getKey());
			parent = node;
			if (cmp > 0) {
				node = node.getRight();
			} else if (cmp == 0) {
				node.setValue(value);
				return;
			} else {
				node = node.getLeft();
			}
		} while (node != null);
		node = new RBNode(key, value == null ? key : value, parent, RED);
		if (cmp > 0) {
			parent.setRight(node);
		} else {
			parent.setLeft(node);
		}

		// 添加完之后调整
		AfterFixPut1(node);

	}

	public void AfterFixPut1(RBNode x) {

		while (x != null && x != root && colorOf(parentOf(x)) == RED) {

			if (colorOf(rightOf(parentOf(parentOf(x)))) == RED && colorOf(leftOf(parentOf(parentOf(x)))) == RED) {
				setColor(leftOf(parentOf(parentOf(x))), BLACK);
				setColor(rightOf(parentOf(parentOf(x))), BLACK);
				setColor(parentOf(parentOf(x)), RED);
				x = parentOf(parentOf(x));
			} else if (leftOf(parentOf(parentOf(x))) == parentOf(x)) {
				// 判断是左左左情况还是左左右，左左右进行左旋转换为左左左
				if (rightOf(parentOf(x)) == x) {
					leftRotate(parentOf(x));
					x = leftOf(x);
				}
				setColor(parentOf(x), BLACK);
				setColor(parentOf(parentOf(x)), RED);
				rightRotate(parentOf(parentOf(x)));
			} else if (rightOf(parentOf(parentOf(x))) == parentOf(x)) {
				// 判断是右右右情况还是右右左，右右左进行右旋转换为右右右
				if (leftOf(parentOf(x)) == x) {
					rightRotate(parentOf(x));
					x = rightOf(x);
				}
				setColor(parentOf(x), BLACK);
				setColor(parentOf(parentOf(x)), RED);
				leftRotate(parentOf(parentOf(x)));
			}

		}

		setColor(root, BLACK);
	}


	/**
	 * 情况一：父节点为黑节点
	 * 情况二：父节点为红节点
	 * 情况三：父节点和叔叔节点都为红节点
	 *
	 * @param node
	 * @author Spirit
	 * @date 2021/11/26 20:52
	 */
	private void AfterFixPut(RBNode node) {
		while (node != null && node != root && parentOf(node).isColor() == RED) {
			// 获取旋转节点，爷爷节点
			RBNode pParent = parentOf(parentOf(node));
			// 判断当前节点是爷爷节点的左节点还是右节点
			if (parentOf(node) == leftOf(pParent)) {
				// 叔父节点都为红色的情况
				if (colorOf(rightOf(pParent)) == RED) {
					setColor(rightOf(pParent), BLACK);
					setColor(leftOf(pParent), BLACK);
					setColor(pParent, RED);
					// 递归处理
					node = pParent;
				} else {
					// 两种情况：左左左和左左右。
					// 左左右需要先局部左旋，转换为左左左的情况
					if (rightOf(parentOf(node)) == node) {
						leftRotate(parentOf(node));
						node = leftOf(node);
					}
					// 左左左：为了保证红黑树的平衡，先进行变色，在进行左旋
					parentOf(node).setColor(pParent.isColor());
					pParent.setColor(RED);
					rightRotate(pParent);
				}

			} else {

				// 叔父节点都是红色节点的情况
				if (colorOf(leftOf(pParent)) == RED) {
					setColor(leftOf(pParent), BLACK);
					setColor(rightOf(pParent), BLACK);
					setColor(pParent, RED);
					// 递归处理
					node = pParent;
				} else {
					// 右右右和右右左的情况
					if (leftOf(parentOf(node)) == node) {
						/*node = parentOf(node);
						System.out.println(node.getKey() + "-->" + node.getParent());*/
						rightRotate(parentOf(node));
						node = rightOf(node);
					}
					setColor(parentOf(node), colorOf(pParent));
					setColor(pParent, RED);
					leftRotate(pParent);
				}
			}
		}

		// 根节点直接赋黑色
		root.setColor(BLACK);
	}

	// 左旋
	public void leftRotate(RBNode p) {

		if (p != null) {
			/*
			 * 左旋节点(p)的右子节点(r)，此节点(r)在左旋完毕后会作为父节点，所以会出现以下情况：
			 * 	1、r不存在左子节点，此时只需要去处理正常调整后的关系
			 * 	2、r存在左子节点，那么在左旋完后r的左子节点就要移动至p的右子节点处
			 */

			RBNode r = rightOf(p);

			// 判断是否存在左子节点，存在，将此节点作为左旋节点的右子节点，并将左旋节点作为此节点的父结点
			if (leftOf(r) != null) {
				p.setRight(leftOf(r));
				leftOf(r).setParent(p);
			} else {
				p.setRight(null);
			}

			if (parentOf(p) != null) {
				// 判断左旋节点是其父节点的左节点还是右节点，根据具体情况将左旋后的节点(r)进行拼接
				if (parentOf(p).getLeft() == p) {
					parentOf(p).setLeft(r);
				} else if (parentOf(p).getRight() == p) {
					parentOf(p).setRight(r);
				}
			} else {
				root = r;
				root.setColor(BLACK);
			}

			// 最终调整，拼接左旋节点(p)和右子节点(r)的关系
			r.setParent(parentOf(p));
			r.setLeft(p);
			p.setParent(r);
		}

	}

	// 右旋
	public void rightRotate(RBNode p) {

		if (p != null) {

			RBNode l = leftOf(p);

			if (rightOf(l) != null) {
				p.setLeft(rightOf(l));
				rightOf(l).setParent(p);
			} else {
				p.setLeft(null);
			}

			if (parentOf(p) != null) {
				if (parentOf(p).getLeft() == p) {
					parentOf(p).setLeft(l);
				} else if (parentOf(p).getRight() == p) {
					parentOf(p).setRight(l);
				}
			} else {
				root = l;
				root.setColor(BLACK);
			}

			l.setParent(parentOf(p));
			p.setParent(l);
			l.setRight(p);

		}

	}


	// 中序遍历，左根右
	public void MiddleOrder(RBNode node) {
		if (node != null) {
			if (leftOf(node) != null) {
				MiddleOrder(leftOf(node));
			}
			System.out.println(node.getKey());
			if (rightOf(node) != null) {
				MiddleOrder(rightOf(node));
			}
		}
	}

	// 先序遍历，根左右
	public void PreambleOrder(RBNode node) {
		if (node != null) {
			System.out.println(node.getKey());
			if (leftOf(node) != null) {
				PreambleOrder(leftOf(node));
			}
			if (rightOf(node) != null) {
				PreambleOrder(rightOf(node));
			}
		}
	}

	// 后序遍历，左右根
	public void PostOrder(RBNode node) {
		if (node != null) {
			if (leftOf(node) != null) {
				PostOrder(leftOf(node));
			}
			if (rightOf(node) != null) {
				PostOrder(rightOf(node));
			}
			System.out.println(node.getKey());
		}
	}

}
