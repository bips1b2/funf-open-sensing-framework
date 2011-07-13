package edu.mit.media.hd.funf.probe;

import android.content.Context;
import android.database.Cursor;
import edu.mit.media.hd.funf.HashUtil;

public abstract class CursorCell<T> {
	public abstract T getData(Cursor cursor, int columnIndex);
	public T getData(Cursor cursor, String columnName) {
		int index = cursor.getColumnIndex(columnName);
		if (index < 0) {
			return null; // Different devices have different columns available
		}
		return getData(cursor, cursor.getColumnIndex(columnName));
	}
	
	public static class StringCell extends CursorCell<String> {
		public String getData(Cursor cursor, int columnIndex) {
			return cursor.getString(columnIndex);
		}
	}
	
	public static class BooleanCell extends CursorCell<Boolean> {
		public Boolean getData(Cursor cursor, int columnIndex) {
			return cursor.getInt(columnIndex) != 0;
		}
	}
	
	public static class ShortCell extends CursorCell<Short> {
		public Short getData(Cursor cursor, int columnIndex) {
			return cursor.getShort(columnIndex);
		}
	}
	
	public static class IntCell extends CursorCell<Integer> {
		public Integer getData(Cursor cursor, int columnIndex) {
			return cursor.getInt(columnIndex);
		}
	}
	
	public static class LongCell extends CursorCell<Long> {
		public Long getData(Cursor cursor, int columnIndex) {
			return cursor.getLong(columnIndex);
		}
	}
	
	public static class FloatCell extends CursorCell<Float> {
		public Float getData(Cursor cursor, int columnIndex) {
			return cursor.getFloat(columnIndex);
		}
	}
	public static class DoubleCell extends CursorCell<Double> {
		public Double getData(Cursor cursor, int columnIndex) {
			return cursor.getDouble(columnIndex);
		}
	}
	
	/**
	 * Always return null.  Used to block data from being returned.
	 */
	public static class NullCell extends CursorCell<Object> {
		@Override
		public Object getData(Cursor cursor, int columnIndex) {
			return null;
		}
	}
	
	/**
	 * Try every type until success
	 */
	public static class AnyCell extends CursorCell<Object> {
		@Override
		public Object getData(Cursor cursor, int columnIndex) {
			if (cursor.isNull(columnIndex)) {
				return null;
			}
			try { return cursor.getShort(columnIndex); } catch (Exception e) {}
			try { return cursor.getInt(columnIndex); } catch (Exception e) {}
			try { return cursor.getLong(columnIndex); } catch (Exception e) {}
			try { return cursor.getFloat(columnIndex); } catch (Exception e) {}
			try { return cursor.getDouble(columnIndex); } catch (Exception e) {}
			try { return cursor.getString(columnIndex); } catch (Exception e) {}
			// Not returning blobs to contain size of bundles and prevent FAILED BINDER TRANSACTION
			//try { return cursor.getBlob(columnIndex); } catch (Exception e) {}
			return null;
		}
	}
	
	public static class PhoneNumberCell extends StringCell {
		@Override
		public String getData(Cursor cursor, int columnIndex) {
			return HashUtil.formatPhoneNumber(super.getData(cursor, columnIndex));
		}
	}
	
	/**
	 * TODO: redo this to pass in the hash strategy
	 *
	 */
	public static class HashedCell extends CursorCell<String> {
		
		private final CursorCell<?> upstreamCell;
		private final Context context;
		
		public HashedCell(Context context, CursorCell<?> upstreamCell) {
			this.upstreamCell = upstreamCell;
			this.context = context;
		}
		
		public String getData(Cursor cursor, int columnIndex) {
			Object value = upstreamCell.getData(cursor, columnIndex);
			return (value == null) ? "" : HashUtil.hashString(context, String.valueOf(value));
		}
	}
}