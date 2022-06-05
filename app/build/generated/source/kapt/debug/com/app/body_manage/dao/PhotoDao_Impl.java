package com.app.body_manage.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.app.body_manage.model.PhotoEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@SuppressWarnings({"unchecked", "deprecation"})
public final class PhotoDao_Impl implements PhotoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PhotoEntity> __insertionAdapterOfPhotoEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeletePhotos;

  public PhotoDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPhotoEntity = new EntityInsertionAdapter<PhotoEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `photos` (`ui`,`body_measure_id`,`photo_uri`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, PhotoEntity value) {
        stmt.bindLong(1, value.getUi());
        stmt.bindLong(2, value.getBodyMeasureId());
        if (value.getPhotoUri() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getPhotoUri());
        }
      }
    };
    this.__preparedStmtOfDeletePhotos = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM photos WHERE body_measure_id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final List<PhotoEntity> photos,
      final Continuation<? super List<Long>> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          List<Long> _result = __insertionAdapterOfPhotoEntity.insertAndReturnIdsList(photos);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object deletePhotos(final int bodyMeasureId,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePhotos.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, bodyMeasureId);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeletePhotos.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object selectPhotos(final int bodyMeasureId,
      final Continuation<? super List<PhotoEntity>> continuation) {
    final String _sql = "SELECT * FROM photos WHERE body_measure_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bodyMeasureId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PhotoEntity>>() {
      @Override
      public List<PhotoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfUi = CursorUtil.getColumnIndexOrThrow(_cursor, "ui");
          final int _cursorIndexOfBodyMeasureId = CursorUtil.getColumnIndexOrThrow(_cursor, "body_measure_id");
          final int _cursorIndexOfPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_uri");
          final List<PhotoEntity> _result = new ArrayList<PhotoEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final PhotoEntity _item;
            final int _tmpUi;
            _tmpUi = _cursor.getInt(_cursorIndexOfUi);
            final int _tmpBodyMeasureId;
            _tmpBodyMeasureId = _cursor.getInt(_cursorIndexOfBodyMeasureId);
            final String _tmpPhotoUri;
            if (_cursor.isNull(_cursorIndexOfPhotoUri)) {
              _tmpPhotoUri = null;
            } else {
              _tmpPhotoUri = _cursor.getString(_cursorIndexOfPhotoUri);
            }
            _item = new PhotoEntity(_tmpUi,_tmpBodyMeasureId,_tmpPhotoUri);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, continuation);
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
