package com.app.body_manage.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.app.body_manage.database.LocalDateConverter;
import com.app.body_manage.model.BodyMeasureEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.coroutines.Continuation;

@SuppressWarnings({"unchecked", "deprecation"})
public final class BodyMeasureDao_Impl implements BodyMeasureDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BodyMeasureEntity> __insertionAdapterOfBodyMeasureEntity;

  private final LocalDateConverter __localDateConverter = new LocalDateConverter();

  private final EntityDeletionOrUpdateAdapter<BodyMeasureEntity> __updateAdapterOfBodyMeasureEntity;

  public BodyMeasureDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBodyMeasureEntity = new EntityInsertionAdapter<BodyMeasureEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `bodyMeasures` (`ui`,`calendar_date`,`capture_date`,`capture_time`,`weight`,`fat`,`photo_uri`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, BodyMeasureEntity value) {
        stmt.bindLong(1, value.getUi());
        final String _tmp = __localDateConverter.fromLocalDate(value.getCalendarDate());
        if (_tmp == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, _tmp);
        }
        final String _tmp_1 = __localDateConverter.fromLocalDate(value.getCapturedDate());
        if (_tmp_1 == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, _tmp_1);
        }
        final String _tmp_2 = __localDateConverter.fromLocalDateTime(value.getCapturedTime());
        if (_tmp_2 == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, _tmp_2);
        }
        stmt.bindDouble(5, value.getWeight());
        stmt.bindDouble(6, value.getFatRate());
        if (value.getPhotoUri() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getPhotoUri());
        }
      }
    };
    this.__updateAdapterOfBodyMeasureEntity = new EntityDeletionOrUpdateAdapter<BodyMeasureEntity>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `bodyMeasures` SET `ui` = ?,`calendar_date` = ?,`capture_date` = ?,`capture_time` = ?,`weight` = ?,`fat` = ?,`photo_uri` = ? WHERE `ui` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, BodyMeasureEntity value) {
        stmt.bindLong(1, value.getUi());
        final String _tmp = __localDateConverter.fromLocalDate(value.getCalendarDate());
        if (_tmp == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, _tmp);
        }
        final String _tmp_1 = __localDateConverter.fromLocalDate(value.getCapturedDate());
        if (_tmp_1 == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, _tmp_1);
        }
        final String _tmp_2 = __localDateConverter.fromLocalDateTime(value.getCapturedTime());
        if (_tmp_2 == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, _tmp_2);
        }
        stmt.bindDouble(5, value.getWeight());
        stmt.bindDouble(6, value.getFatRate());
        if (value.getPhotoUri() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getPhotoUri());
        }
        stmt.bindLong(8, value.getUi());
      }
    };
  }

  @Override
  public Object insert(final BodyMeasureEntity bodyMeasureEntity,
      final Continuation<? super Long> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          long _result = __insertionAdapterOfBodyMeasureEntity.insertAndReturnId(bodyMeasureEntity);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object update(final BodyMeasureEntity bodyMeasureEntity,
      final Continuation<? super Integer> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      public Integer call() throws Exception {
        int _total = 0;
        __db.beginTransaction();
        try {
          _total +=__updateAdapterOfBodyMeasureEntity.handle(bodyMeasureEntity);
          __db.setTransactionSuccessful();
          return _total;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object getTrainingEntityListByDate(final LocalDate calendarDate,
      final Continuation<? super List<BodyMeasureEntity>> continuation) {
    final String _sql = "SELECT * FROM bodyMeasures WHERE calendar_date = ? ORDER BY capture_time ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __localDateConverter.fromLocalDate(calendarDate);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BodyMeasureEntity>>() {
      @Override
      public List<BodyMeasureEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfUi = CursorUtil.getColumnIndexOrThrow(_cursor, "ui");
          final int _cursorIndexOfCalendarDate = CursorUtil.getColumnIndexOrThrow(_cursor, "calendar_date");
          final int _cursorIndexOfCapturedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "capture_date");
          final int _cursorIndexOfCapturedTime = CursorUtil.getColumnIndexOrThrow(_cursor, "capture_time");
          final int _cursorIndexOfWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "weight");
          final int _cursorIndexOfFatRate = CursorUtil.getColumnIndexOrThrow(_cursor, "fat");
          final int _cursorIndexOfPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_uri");
          final List<BodyMeasureEntity> _result = new ArrayList<BodyMeasureEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final BodyMeasureEntity _item;
            final int _tmpUi;
            _tmpUi = _cursor.getInt(_cursorIndexOfUi);
            final LocalDate _tmpCalendarDate;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfCalendarDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfCalendarDate);
            }
            _tmpCalendarDate = __localDateConverter.toLocalDate(_tmp_1);
            final LocalDate _tmpCapturedDate;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfCapturedDate)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfCapturedDate);
            }
            _tmpCapturedDate = __localDateConverter.toLocalDate(_tmp_2);
            final LocalDateTime _tmpCapturedTime;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfCapturedTime)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfCapturedTime);
            }
            _tmpCapturedTime = __localDateConverter.toLocalDateTime(_tmp_3);
            final float _tmpWeight;
            _tmpWeight = _cursor.getFloat(_cursorIndexOfWeight);
            final float _tmpFatRate;
            _tmpFatRate = _cursor.getFloat(_cursorIndexOfFatRate);
            final String _tmpPhotoUri;
            if (_cursor.isNull(_cursorIndexOfPhotoUri)) {
              _tmpPhotoUri = null;
            } else {
              _tmpPhotoUri = _cursor.getString(_cursorIndexOfPhotoUri);
            }
            _item = new BodyMeasureEntity(_tmpUi,_tmpCalendarDate,_tmpCapturedDate,_tmpCapturedTime,_tmpWeight,_tmpFatRate,_tmpPhotoUri);
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

  @Override
  public Object getTrainingEntityListBetween(final LocalDateTime startDateTime,
      final LocalDateTime endDateTime,
      final Continuation<? super List<BodyMeasureEntity>> continuation) {
    final String _sql = "SELECT ui, calendar_date, capture_date, capture_time, AVG(weight) as weight, AVG(fat) as fat, photo_uri FROM bodyMeasures WHERE capture_date BETWEEN ? AND ? GROUP BY bodyMeasures.calendar_date";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final String _tmp = __localDateConverter.fromLocalDateTime(startDateTime);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    _argIndex = 2;
    final String _tmp_1 = __localDateConverter.fromLocalDateTime(endDateTime);
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp_1);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BodyMeasureEntity>>() {
      @Override
      public List<BodyMeasureEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfUi = 0;
          final int _cursorIndexOfCalendarDate = 1;
          final int _cursorIndexOfCapturedDate = 2;
          final int _cursorIndexOfCapturedTime = 3;
          final int _cursorIndexOfWeight = 4;
          final int _cursorIndexOfFatRate = 5;
          final int _cursorIndexOfPhotoUri = 6;
          final List<BodyMeasureEntity> _result = new ArrayList<BodyMeasureEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final BodyMeasureEntity _item;
            final int _tmpUi;
            _tmpUi = _cursor.getInt(_cursorIndexOfUi);
            final LocalDate _tmpCalendarDate;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfCalendarDate)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfCalendarDate);
            }
            _tmpCalendarDate = __localDateConverter.toLocalDate(_tmp_2);
            final LocalDate _tmpCapturedDate;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfCapturedDate)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfCapturedDate);
            }
            _tmpCapturedDate = __localDateConverter.toLocalDate(_tmp_3);
            final LocalDateTime _tmpCapturedTime;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfCapturedTime)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfCapturedTime);
            }
            _tmpCapturedTime = __localDateConverter.toLocalDateTime(_tmp_4);
            final float _tmpWeight;
            _tmpWeight = _cursor.getFloat(_cursorIndexOfWeight);
            final float _tmpFatRate;
            _tmpFatRate = _cursor.getFloat(_cursorIndexOfFatRate);
            final String _tmpPhotoUri;
            if (_cursor.isNull(_cursorIndexOfPhotoUri)) {
              _tmpPhotoUri = null;
            } else {
              _tmpPhotoUri = _cursor.getString(_cursorIndexOfPhotoUri);
            }
            _item = new BodyMeasureEntity(_tmpUi,_tmpCalendarDate,_tmpCapturedDate,_tmpCapturedTime,_tmpWeight,_tmpFatRate,_tmpPhotoUri);
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

  @Override
  public Object getTrainingEntityList(
      final Continuation<? super List<BodyMeasureEntity>> continuation) {
    final String _sql = "SELECT * FROM bodyMeasures WHERE capture_time";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BodyMeasureEntity>>() {
      @Override
      public List<BodyMeasureEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfUi = CursorUtil.getColumnIndexOrThrow(_cursor, "ui");
          final int _cursorIndexOfCalendarDate = CursorUtil.getColumnIndexOrThrow(_cursor, "calendar_date");
          final int _cursorIndexOfCapturedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "capture_date");
          final int _cursorIndexOfCapturedTime = CursorUtil.getColumnIndexOrThrow(_cursor, "capture_time");
          final int _cursorIndexOfWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "weight");
          final int _cursorIndexOfFatRate = CursorUtil.getColumnIndexOrThrow(_cursor, "fat");
          final int _cursorIndexOfPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_uri");
          final List<BodyMeasureEntity> _result = new ArrayList<BodyMeasureEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final BodyMeasureEntity _item;
            final int _tmpUi;
            _tmpUi = _cursor.getInt(_cursorIndexOfUi);
            final LocalDate _tmpCalendarDate;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfCalendarDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCalendarDate);
            }
            _tmpCalendarDate = __localDateConverter.toLocalDate(_tmp);
            final LocalDate _tmpCapturedDate;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfCapturedDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfCapturedDate);
            }
            _tmpCapturedDate = __localDateConverter.toLocalDate(_tmp_1);
            final LocalDateTime _tmpCapturedTime;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfCapturedTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfCapturedTime);
            }
            _tmpCapturedTime = __localDateConverter.toLocalDateTime(_tmp_2);
            final float _tmpWeight;
            _tmpWeight = _cursor.getFloat(_cursorIndexOfWeight);
            final float _tmpFatRate;
            _tmpFatRate = _cursor.getFloat(_cursorIndexOfFatRate);
            final String _tmpPhotoUri;
            if (_cursor.isNull(_cursorIndexOfPhotoUri)) {
              _tmpPhotoUri = null;
            } else {
              _tmpPhotoUri = _cursor.getString(_cursorIndexOfPhotoUri);
            }
            _item = new BodyMeasureEntity(_tmpUi,_tmpCalendarDate,_tmpCapturedDate,_tmpCapturedTime,_tmpWeight,_tmpFatRate,_tmpPhotoUri);
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

  @Override
  public Object getTrainingEntityByLocalDateTime(final LocalDateTime captureTime,
      final Continuation<? super List<BodyMeasureEntity>> continuation) {
    final String _sql = "SELECT * FROM bodyMeasures WHERE capture_time = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __localDateConverter.fromLocalDateTime(captureTime);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BodyMeasureEntity>>() {
      @Override
      public List<BodyMeasureEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfUi = CursorUtil.getColumnIndexOrThrow(_cursor, "ui");
          final int _cursorIndexOfCalendarDate = CursorUtil.getColumnIndexOrThrow(_cursor, "calendar_date");
          final int _cursorIndexOfCapturedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "capture_date");
          final int _cursorIndexOfCapturedTime = CursorUtil.getColumnIndexOrThrow(_cursor, "capture_time");
          final int _cursorIndexOfWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "weight");
          final int _cursorIndexOfFatRate = CursorUtil.getColumnIndexOrThrow(_cursor, "fat");
          final int _cursorIndexOfPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "photo_uri");
          final List<BodyMeasureEntity> _result = new ArrayList<BodyMeasureEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final BodyMeasureEntity _item;
            final int _tmpUi;
            _tmpUi = _cursor.getInt(_cursorIndexOfUi);
            final LocalDate _tmpCalendarDate;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfCalendarDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfCalendarDate);
            }
            _tmpCalendarDate = __localDateConverter.toLocalDate(_tmp_1);
            final LocalDate _tmpCapturedDate;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfCapturedDate)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfCapturedDate);
            }
            _tmpCapturedDate = __localDateConverter.toLocalDate(_tmp_2);
            final LocalDateTime _tmpCapturedTime;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfCapturedTime)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfCapturedTime);
            }
            _tmpCapturedTime = __localDateConverter.toLocalDateTime(_tmp_3);
            final float _tmpWeight;
            _tmpWeight = _cursor.getFloat(_cursorIndexOfWeight);
            final float _tmpFatRate;
            _tmpFatRate = _cursor.getFloat(_cursorIndexOfFatRate);
            final String _tmpPhotoUri;
            if (_cursor.isNull(_cursorIndexOfPhotoUri)) {
              _tmpPhotoUri = null;
            } else {
              _tmpPhotoUri = _cursor.getString(_cursorIndexOfPhotoUri);
            }
            _item = new BodyMeasureEntity(_tmpUi,_tmpCalendarDate,_tmpCapturedDate,_tmpCapturedTime,_tmpWeight,_tmpFatRate,_tmpPhotoUri);
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
