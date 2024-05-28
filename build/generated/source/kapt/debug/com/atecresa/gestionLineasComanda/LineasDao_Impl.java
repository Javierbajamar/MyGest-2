package com.atecresa.gestionLineasComanda;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class LineasDao_Impl implements LineasDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<LineasComanda> __insertionAdapterOfLineasComanda;

  private final SharedSQLiteStatement __preparedStmtOfDeleteLineasPendientes;

  private final SharedSQLiteStatement __preparedStmtOfDeleteLineasRetenidas;

  public LineasDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLineasComanda = new EntityInsertionAdapter<LineasComanda>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `LINEAS_COMANDA` (`MESA`,`ESTADO`,`JSON`,`id`) VALUES (?,?,?,nullif(?, 0))";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LineasComanda entity) {
        if (entity.getMesa() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getMesa());
        }
        statement.bindLong(2, entity.getEstado());
        if (entity.getJson() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getJson());
        }
        statement.bindLong(4, entity.getContactId());
      }
    };
    this.__preparedStmtOfDeleteLineasPendientes = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM LINEAS_COMANDA WHERE ESTADO=101";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteLineasRetenidas = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM LINEAS_COMANDA WHERE ESTADO=100 AND MESA=?";
        return _query;
      }
    };
  }

  @Override
  public void insertLinea(final LineasComanda linea) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfLineasComanda.insert(linea);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteLineasPendientes() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteLineasPendientes.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteLineasPendientes.release(_stmt);
    }
  }

  @Override
  public void deleteLineasRetenidas(final String mesa) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteLineasRetenidas.acquire();
    int _argIndex = 1;
    if (mesa == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, mesa);
    }
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteLineasRetenidas.release(_stmt);
    }
  }

  @Override
  public LiveData<List<LineasComanda>> getLineasPendientes() {
    final String _sql = "SELECT * from LINEAS_COMANDA WHERE ESTADO=101";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"LINEAS_COMANDA"}, false, new Callable<List<LineasComanda>>() {
      @Override
      @Nullable
      public List<LineasComanda> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMesa = CursorUtil.getColumnIndexOrThrow(_cursor, "MESA");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "ESTADO");
          final int _cursorIndexOfJson = CursorUtil.getColumnIndexOrThrow(_cursor, "JSON");
          final int _cursorIndexOfContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final List<LineasComanda> _result = new ArrayList<LineasComanda>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LineasComanda _item;
            final String _tmpMesa;
            if (_cursor.isNull(_cursorIndexOfMesa)) {
              _tmpMesa = null;
            } else {
              _tmpMesa = _cursor.getString(_cursorIndexOfMesa);
            }
            final int _tmpEstado;
            _tmpEstado = _cursor.getInt(_cursorIndexOfEstado);
            final String _tmpJson;
            if (_cursor.isNull(_cursorIndexOfJson)) {
              _tmpJson = null;
            } else {
              _tmpJson = _cursor.getString(_cursorIndexOfJson);
            }
            _item = new LineasComanda(_tmpMesa,_tmpEstado,_tmpJson);
            final int _tmpContactId;
            _tmpContactId = _cursor.getInt(_cursorIndexOfContactId);
            _item.setContactId(_tmpContactId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<LineasComanda>> getLineasRetenidas() {
    final String _sql = "SELECT * FROM LINEAS_COMANDA WHERE ESTADO=100";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"LINEAS_COMANDA"}, false, new Callable<List<LineasComanda>>() {
      @Override
      @Nullable
      public List<LineasComanda> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMesa = CursorUtil.getColumnIndexOrThrow(_cursor, "MESA");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "ESTADO");
          final int _cursorIndexOfJson = CursorUtil.getColumnIndexOrThrow(_cursor, "JSON");
          final int _cursorIndexOfContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final List<LineasComanda> _result = new ArrayList<LineasComanda>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LineasComanda _item;
            final String _tmpMesa;
            if (_cursor.isNull(_cursorIndexOfMesa)) {
              _tmpMesa = null;
            } else {
              _tmpMesa = _cursor.getString(_cursorIndexOfMesa);
            }
            final int _tmpEstado;
            _tmpEstado = _cursor.getInt(_cursorIndexOfEstado);
            final String _tmpJson;
            if (_cursor.isNull(_cursorIndexOfJson)) {
              _tmpJson = null;
            } else {
              _tmpJson = _cursor.getString(_cursorIndexOfJson);
            }
            _item = new LineasComanda(_tmpMesa,_tmpEstado,_tmpJson);
            final int _tmpContactId;
            _tmpContactId = _cursor.getInt(_cursorIndexOfContactId);
            _item.setContactId(_tmpContactId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<LineasComanda>> getLineasRetenidasPorMesa(final String mesa) {
    final String _sql = "SELECT * from LINEAS_COMANDA WHERE MESA=? AND ESTADO=100";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (mesa == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, mesa);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"LINEAS_COMANDA"}, false, new Callable<List<LineasComanda>>() {
      @Override
      @Nullable
      public List<LineasComanda> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMesa = CursorUtil.getColumnIndexOrThrow(_cursor, "MESA");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "ESTADO");
          final int _cursorIndexOfJson = CursorUtil.getColumnIndexOrThrow(_cursor, "JSON");
          final int _cursorIndexOfContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final List<LineasComanda> _result = new ArrayList<LineasComanda>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LineasComanda _item;
            final String _tmpMesa;
            if (_cursor.isNull(_cursorIndexOfMesa)) {
              _tmpMesa = null;
            } else {
              _tmpMesa = _cursor.getString(_cursorIndexOfMesa);
            }
            final int _tmpEstado;
            _tmpEstado = _cursor.getInt(_cursorIndexOfEstado);
            final String _tmpJson;
            if (_cursor.isNull(_cursorIndexOfJson)) {
              _tmpJson = null;
            } else {
              _tmpJson = _cursor.getString(_cursorIndexOfJson);
            }
            _item = new LineasComanda(_tmpMesa,_tmpEstado,_tmpJson);
            final int _tmpContactId;
            _tmpContactId = _cursor.getInt(_cursorIndexOfContactId);
            _item.setContactId(_tmpContactId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
