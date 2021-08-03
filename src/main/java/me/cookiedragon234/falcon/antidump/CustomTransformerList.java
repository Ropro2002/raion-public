package me.cookiedragon234.falcon.antidump;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.ModAPITransformer;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author cookiedragon234 04/Jan/2020
 */
public class CustomTransformerList implements List<IClassTransformer> {
	private final List<IClassTransformer> original;
	private final IClassTransformer special;
	
	public CustomTransformerList(List<IClassTransformer> original, IClassTransformer special) {
		this.original = original;
		this.special = special;
	}
	
	private void sort(IClassTransformer addedTransformer) {
		if (!ModAPITransformer.class.isAssignableFrom(addedTransformer.getClass())) {
			this.original.remove(special);
			this.original.add(special);
		}
	}
	
	@Override
	public int size() {
		return original.size();
	}
	
	@Override
	public boolean isEmpty() {
		return original.isEmpty();
	}
	
	@Override
	public boolean contains(Object o) {
		return original.contains(o);
	}
	
	@Override
	public Iterator<IClassTransformer> iterator() {
		return original.iterator();
	}
	
	@Override
	public Object[] toArray() {
		return original.toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		return original.toArray(a);
	}
	
	@Override
	public boolean add(IClassTransformer transformer) {
		boolean add = original.add(transformer);
		sort(transformer);
		return add;
	}
	
	@Override
	public boolean remove(Object o) {
		return original.remove(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return original.containsAll(c);
	}
	
	@Override
	public boolean addAll(Collection<? extends IClassTransformer> c) {
		return original.addAll(c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends IClassTransformer> c) {
		return original.addAll(index, c);
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		return original.removeAll(c);
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		return original.retainAll(c);
	}
	
	@Override
	public void clear() {
		original.clear();
	}
	
	@Override
	public IClassTransformer get(int index) {
		return original.get(index);
	}
	
	@Override
	public IClassTransformer set(int index, IClassTransformer element) {
		return original.set(index, element);
	}
	
	@Override
	public void add(int index, IClassTransformer element) {
		original.add(index, element);
	}
	
	@Override
	public IClassTransformer remove(int index) {
		return original.remove(index);
	}
	
	@Override
	public int indexOf(Object o) {
		return original.indexOf(o);
	}
	
	@Override
	public int lastIndexOf(Object o) {
		return original.lastIndexOf(o);
	}
	
	@Override
	public ListIterator<IClassTransformer> listIterator() {
		return original.listIterator();
	}
	
	@Override
	public ListIterator<IClassTransformer> listIterator(int index) {
		return original.listIterator(index);
	}
	
	@Override
	public List<IClassTransformer> subList(int fromIndex, int toIndex) {
		return original.subList(fromIndex, toIndex);
	}
}
