package telran.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class TreeSet<T> implements SortedSet<T> {
	private static class Node<T> {
		T data;
		Node<T> parent;
		Node<T> left;
		Node<T> right;

		Node(T data) {
			this.data = data;
		}

	}

	private class TreeSetIterator implements Iterator<T> {
		Node<T> current = getLeastFrom(root);

		@Override
		public boolean hasNext() {

			return current != null;
		}

		@Override
		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			T res = current.data;
			current = getCurrent(current);
			return res;
		}

	}

	Node<T> root;
	int size;
	private Comparator<T> comp;

	public TreeSet(Comparator<T> comp) {
		this.comp = comp;
	}

	@SuppressWarnings("unchecked")
	public TreeSet() {
		this((Comparator<T>) Comparator.naturalOrder());
	}

	@Override
	public T get(T pattern) {
		Node<T> node = getNode(pattern);

		return node == null ? null : node.data;
	}

	private Node<T> getNode(T pattern) {
		Node<T> res = null;
		Node<T> node = getParentOrNode(pattern);
		if (node != null && comp.compare(node.data, pattern) == 0) {
			res = node;
		}
		return res;
	}

	private Node<T> getParentOrNode(T pattern) {
		Node<T> current = root;
		Node<T> parent = null;
		int compRes = 0;
		while (current != null && (compRes = comp.compare(pattern, current.data)) != 0) {
			parent = current;
			current = compRes > 0 ? current.right : current.left;
		}
		return current == null ? parent : current;
	}

	public Node<T> getCurrent(Node<T> current) {
		return current.right != null ? getLeastFrom(current.right) : getFirstGreaterParent(current);
	}

	private Node<T> getFirstGreaterParent(Node<T> current) {
		Node<T> parent = current.parent;
		while (parent != null && comp.compare(parent.data, current.data) < 0) {
			current = parent;
			parent = current.parent;
		}

		return parent;
	}

	private Node<T> getLeastFrom(Node<T> node) {
		if (node != null) {

			while (node.left != null) {
				node = node.left;
			}
		}
		return node;
	}

	@Override
	public boolean add(T obj) {
		boolean res = false;
		if (obj == null) {
			throw new NullPointerException();
		}
		if (!contains(obj)) {
			res = true;
			Node<T> node = new Node<>(obj);
			if (root == null) {
				addRoot(node);
			} else {
				addWithParent(node);
			}
			size++;
		}
		return res;
	}

	private void addWithParent(Node<T> node) {
		Node<T> parent = getParent(node);
		if (comp.compare(node.data, parent.data) > 0) {
			parent.right = node;
		} else {
			parent.left = node;
		}
		node.parent = parent;

	}

	private Node<T> getParent(Node<T> node) {
		Node<T> parent = getParentOrNode(node.data);
		return parent;
	}

	private void addRoot(Node<T> node) {
		root = node;

	}

	@Override
	public boolean remove(T pattern) {
		boolean res = false;
		Node<T> node = getNode(pattern);
		if (node != null) {
			removeNode(node);
			res = true;
		}
		return res;
	}

	private void removeNode(Node<T> node) {

		if (!contains(node.data)) {
			throw new NoSuchElementException();
		}
		if (node == root) {
			removeRoot(node);
		} else if (node.left != null && node.right != null) {
			removeFromTree(node);
		} else {
			removeFromList(node);
		}

	}

	private void removeRoot(Node<T> node) {

		if (node.left == null && node.right == null) {
			clearNode(node);
			root = null;
		} else if (node.left == null && node.right != null) {
			substituteNodes(node, node.right, false);
		} else if ((node.left != null && node.right == null)) {
			substituteNodes(node, node.left, false);

		} else if (node.left != null && node.right != null) {
			Node<T> nodeRight = node.right;
			Node<T> lastRight = getLastRight(node.left);

			substituteNodes(node, node.left, false);

			lastRight.right = nodeRight;
			nodeRight.parent = lastRight;
		}
		size--;

	}

	private Node<T> getLastRight(Node<T> node) {
		if (node != null) {

			while (node.right != null) {
				node = node.right;
			}
		}
		return node;
	}

	private void removeFromTree(Node<T> node) {
		Node<T> nodeRight = node.right;
		Node<T> lastRight = getLastRight(node.left);

		lastRight.right = nodeRight;
		nodeRight.parent = lastRight;
		substituteNodes(node, node.left, false);

		size--;
	}

	private void removeFromList(Node<T> node) {

		if (node.left == null && node.right == null) {
			substituteNodes(node, null, true);
		} else if (node.left == null && node.right != null) {
			substituteNodes(node, node.right, false);
		} else if (node.left != null && node.right == null) {
			substituteNodes(node, node.left, false);
		}
		size--;
	}

	private void substituteNodes(Node<T> node, Node<T> substitute, boolean isNull) {
		if (node.parent == null) {
			node = substitute;
			root = node;
			node.parent = null;
		} else if (isNull) {
			if (comp.compare(node.parent.data, node.data) > 0) {
				node.parent.left = null;
			} else {
				node.parent.right = null;
			}
			clearNode(node);

		} else {
			Node<T> nodeParent = node.parent;

			if (comp.compare(node.parent.data, substitute.data) > 0) {
				node.parent.left = substitute;
			} else {
				node.parent.right = substitute;
			}
			node = substitute;
			node.parent = nodeParent;
		}
	}

	private void clearNode(Node<T> node) {
		node.data = null;
		node.left = null;
		node.right = null;
		node.parent = null;
		node = null;
	}

	@Override
	public boolean contains(T pattern) {

		return getNode(pattern) != null;
	}

	@Override
	public int size() {

		return size;
	}

	@Override
	public Iterator<T> iterator() {

		return new TreeSetIterator();
	}

	@Override
	public T first() {

		return root == null ? null : getLeastFrom(root).data;
	}

	@Override
	public T last() {

		return root == null ? null : getGreatesFrom(root).data;
	}

	private Node<T> getGreatesFrom(Node<T> node) {
		if (node != null) {
			while (node.right != null) {
				node = node.right;
			}
		}
		return node;
	}

}
