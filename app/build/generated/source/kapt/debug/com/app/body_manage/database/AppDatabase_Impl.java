package com.app.body_manage.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.RoomOpenHelper.ValidationResult;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import com.app.body_manage.dao.BodyMeasureDao;
import com.app.body_manage.dao.BodyMeasureDao_Impl;
import com.app.body_manage.dao.BodyMeasurePhotoDao;
import com.app.body_manage.dao.BodyMeasurePhotoDao_Impl;
import com.app.body_manage.dao.PhotoDao;
import com.app.body_manage.dao.PhotoDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile BodyMeasureDao _bodyMeasureDao;

  private volatile PhotoDao _photoDao;

  private volatile BodyMeasurePhotoDao _bodyMeasurePhotoDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `bodyMeasures` (`ui` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `calendar_date` TEXT NOT NULL, `capture_date` TEXT NOT NULL, `capture_time` TEXT NOT NULL, `weight` REAL NOT NULL, `fat_fate` REAL NOT NULL, `photo_uri` TEXT)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `photos` (`ui` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `body_measure_id` INTEGER NOT NULL, `photo_uri` TEXT NOT NULL, FOREIGN KEY(`body_measure_id`) REFERENCES `bodyMeasures`(`ui`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        _db.execSQL("CREATE INDEX IF NOT EXISTS `index_photos_body_measure_id` ON `photos` (`body_measure_id`)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '142973f8b3408e9a95df33d814f5c677')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `bodyMeasures`");
        _db.execSQL("DROP TABLE IF EXISTS `photos`");
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onDestructiveMigration(_db);
          }
        }
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        _db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      protected RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsBodyMeasures = new HashMap<String, TableInfo.Column>(7);
        _columnsBodyMeasures.put("ui", new TableInfo.Column("ui", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBodyMeasures.put("calendar_date", new TableInfo.Column("calendar_date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBodyMeasures.put("capture_date", new TableInfo.Column("capture_date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBodyMeasures.put("capture_time", new TableInfo.Column("capture_time", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBodyMeasures.put("weight", new TableInfo.Column("weight", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBodyMeasures.put("fat_fate", new TableInfo.Column("fat_fate", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBodyMeasures.put("photo_uri", new TableInfo.Column("photo_uri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBodyMeasures = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBodyMeasures = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBodyMeasures = new TableInfo("bodyMeasures", _columnsBodyMeasures, _foreignKeysBodyMeasures, _indicesBodyMeasures);
        final TableInfo _existingBodyMeasures = TableInfo.read(_db, "bodyMeasures");
        if (! _infoBodyMeasures.equals(_existingBodyMeasures)) {
          return new RoomOpenHelper.ValidationResult(false, "bodyMeasures(com.app.body_manage.model.BodyMeasureEntity).\n"
                  + " Expected:\n" + _infoBodyMeasures + "\n"
                  + " Found:\n" + _existingBodyMeasures);
        }
        final HashMap<String, TableInfo.Column> _columnsPhotos = new HashMap<String, TableInfo.Column>(3);
        _columnsPhotos.put("ui", new TableInfo.Column("ui", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("body_measure_id", new TableInfo.Column("body_measure_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("photo_uri", new TableInfo.Column("photo_uri", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPhotos = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysPhotos.add(new TableInfo.ForeignKey("bodyMeasures", "CASCADE", "NO ACTION",Arrays.asList("body_measure_id"), Arrays.asList("ui")));
        final HashSet<TableInfo.Index> _indicesPhotos = new HashSet<TableInfo.Index>(1);
        _indicesPhotos.add(new TableInfo.Index("index_photos_body_measure_id", false, Arrays.asList("body_measure_id"), Arrays.asList("ASC")));
        final TableInfo _infoPhotos = new TableInfo("photos", _columnsPhotos, _foreignKeysPhotos, _indicesPhotos);
        final TableInfo _existingPhotos = TableInfo.read(_db, "photos");
        if (! _infoPhotos.equals(_existingPhotos)) {
          return new RoomOpenHelper.ValidationResult(false, "photos(com.app.body_manage.model.PhotoEntity).\n"
                  + " Expected:\n" + _infoPhotos + "\n"
                  + " Found:\n" + _existingPhotos);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "142973f8b3408e9a95df33d814f5c677", "3cba3a23f22aa7c305c822c786c2314a");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "bodyMeasures","photos");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `bodyMeasures`");
      _db.execSQL("DELETE FROM `photos`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(BodyMeasureDao.class, BodyMeasureDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PhotoDao.class, PhotoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BodyMeasurePhotoDao.class, BodyMeasurePhotoDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  public List<Migration> getAutoMigrations(
      @NonNull Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecsMap) {
    return Arrays.asList();
  }

  @Override
  public BodyMeasureDao bodyMeasureDao() {
    if (_bodyMeasureDao != null) {
      return _bodyMeasureDao;
    } else {
      synchronized(this) {
        if(_bodyMeasureDao == null) {
          _bodyMeasureDao = new BodyMeasureDao_Impl(this);
        }
        return _bodyMeasureDao;
      }
    }
  }

  @Override
  public PhotoDao photoDao() {
    if (_photoDao != null) {
      return _photoDao;
    } else {
      synchronized(this) {
        if(_photoDao == null) {
          _photoDao = new PhotoDao_Impl(this);
        }
        return _photoDao;
      }
    }
  }

  @Override
  public BodyMeasurePhotoDao bodyMeasurePhotoDao() {
    if (_bodyMeasurePhotoDao != null) {
      return _bodyMeasurePhotoDao;
    } else {
      synchronized(this) {
        if(_bodyMeasurePhotoDao == null) {
          _bodyMeasurePhotoDao = new BodyMeasurePhotoDao_Impl(this);
        }
        return _bodyMeasurePhotoDao;
      }
    }
  }
}
