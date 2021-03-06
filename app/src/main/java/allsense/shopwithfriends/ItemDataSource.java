package allsense.shopwithfriends;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ItemDataSource {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    private static final String[] ALL_COLUMNS_ITEMS = {
            SQLiteHelper.ITEMS_COLUMN_ID,
            SQLiteHelper.ITEMS_COLUMN_NAME,
            SQLiteHelper.ITEMS_COLUMN_SELLER,
    };

    private static final String[] ALL_COLUMNS_REPORTED = {
            SQLiteHelper.REPORTED_COLUMN_ITEM_ID,
            SQLiteHelper.REPORTED_COLUMN_FRIEND_ID_1,
            SQLiteHelper.REPORTED_COLUMN_FRIEND_ID_2,
    };

    public ItemDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    /**
     * closes the database
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * returns all columns in the table ITEMS that match the selection
     * @param selection  the condition to match against, null for all rows
     * @return  all rows that match selection
     */
    public Cursor queryItems(final String selection) {
        return database.query(SQLiteHelper.TABLE_ITEMS, ALL_COLUMNS_ITEMS, selection, null, null, null, null);
    }

    public Cursor queryReported(final String selection) {
        return database.query(SQLiteHelper.TABLE_REPORTED, ALL_COLUMNS_REPORTED, selection, null, null, null, null);
    }

    /**
     * returns the item at the cursor position
     * @param cursor
     * @return  the item at the current position of the cursor
     */
    private Item itemAtCursor(Cursor cursor) {
        long id = cursor.getLong(0);
        String name = cursor.getString(1);
        String seller = cursor.getString(2);
        return new Item(name, seller, id);
    }

    /**
     * creates item to be put into database
     * @param name
     * @return the created item
     */
    public Item createItem(final String name, final String seller) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.ITEMS_COLUMN_NAME, name);
        values.put(SQLiteHelper.ITEMS_COLUMN_SELLER, seller);
        long insertID = database.insert(SQLiteHelper.TABLE_ITEMS, null, values);
        Cursor cursor = queryItems(SQLiteHelper.ITEMS_COLUMN_ID + " = " + insertID);
        cursor.moveToFirst();
        Item item = itemAtCursor(cursor);
        cursor.close();
        return item;
    }

    public void reportSale(final Item item, final User friend1, final User friend2) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.REPORTED_COLUMN_ITEM_ID, item.id());
        values.put(SQLiteHelper.REPORTED_COLUMN_FRIEND_ID_1, friend1.id());
        values.put(SQLiteHelper.REPORTED_COLUMN_FRIEND_ID_2, friend2.id());
        database.insert(SQLiteHelper.TABLE_REPORTED, null, values);
    }

    /**
     * deletes the specified item from the database
     * @param item
     */
    public void deleteItem(final Item item) {
        long id = item.id();
        System.out.println("deleting item " + item);
        database.delete(SQLiteHelper.TABLE_ITEMS, SQLiteHelper.ITEMS_COLUMN_ID + " = " + id, null);
    }

    /**
     *
     * @return  a list of all items in the database ITEMS
     */
    public List<Item> allItems() {
        Cursor cursor = queryItems(null);
        return itemsFromCursor(cursor);
    }

    /**
     * returns a list of reported items a user has received
     * @param user
     * @return  a list of reported items a user has received
     */
    public List<Item> reportedTo(final User user) {
        Cursor cursor = queryReported(SQLiteHelper.REPORTED_COLUMN_FRIEND_ID_2 + " = " + user.id());
        return itemsFromCursor(cursor);
    }

    /**
     * returns a list of reported items a user has reported
     * @param user
     * @return  a list of reported items a user has reported
     */
    public List<Item> reportedBy(final User user) {
        Cursor cursor = queryReported(SQLiteHelper.REPORTED_COLUMN_FRIEND_ID_1 + " = " + user.id());
        return itemsFromCursor(cursor);
    }

    /**
     * returns a list of reported items friend 1 has reported to friend 2
     * @param friend1
     * @param friend2
     * @return  a list of reported items friend 1 has reported to friend 2
     */
    public List<Item> reportedFromTo(final User friend1, final User friend2) {
        Cursor cursor = queryReported(SQLiteHelper.REPORTED_COLUMN_FRIEND_ID_1 + " = " + friend1.id()
                + " AND " + SQLiteHelper.REPORTED_COLUMN_FRIEND_ID_2 + " = " + friend2.id());
        return itemsFromCursor(cursor);
    }

    private List<Item> itemsFromCursor(final Cursor cursor) {
        List<Item> items = new ArrayList<Item>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            long itemID = cursor.getLong(1);
            Item item = itemForID(itemID);
            items.add(item);
            cursor.moveToNext();
        }

        cursor.close();

        return items;
    }

    /**
     * finds the item with the id in the table
     * @param id  the id to find
     * @return  the item with the id passed in, null if not found
     */
    public Item itemForID(final long id) {
        Cursor cursor = queryItems(SQLiteHelper.ITEMS_COLUMN_ID + " = " + id);
        cursor.moveToFirst();
        try {
            if (cursor.isAfterLast()) {
                return null;
            } else {
                return itemAtCursor(cursor);
            }
        } finally {
            cursor.close();
        }
    }
}
