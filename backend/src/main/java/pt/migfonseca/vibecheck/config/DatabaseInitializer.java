package pt.migfonseca.vibecheck.config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;


@Service
public class DatabaseInitializer {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    @Transactional
    public void init() {
        initializeDatabase();
    }

    @PostConstruct
    @Transactional
    public void initializeDatabase() {
        String referencedTableSong = "song";
        String referenceIdColumnSong = "song_id";
        String columnTypeSong = "VARCHAR(255)";
        String columnNameSong = "song_name";
        String triggerNameSong = "set_song_name";
        String functionNameSong = "set_song_name";

        String tableNameArtist = "song_artist";
        String uniqueConstraintArtist = "artist_id, song_name";

        String referencedTableAlbumArtist = "album";
        String referenceIdColumnAlbumArtist = "album_id";
        String columnTypeAlbumArtist = "VARCHAR(255)";
        String columnNameAlbumArtist = "album_name";
        String triggerNameAlbumArtist = "set_album_name";
        String functionNameAlbumArtist = "set_album_name";

        String tableNameAlbumArtist = "album_artist";
        String uniqueConstraintAlbumArtist = "artist_id, album_name";

        String tableNameFeatureArtist = "feature_artist";

        try {
            createTableIfNotExists(tableNameArtist, columnNameSong, columnTypeSong);
            addColumnIfNotExists(tableNameArtist, columnNameSong, columnTypeSong);
            addUniqueConstraint(tableNameArtist, uniqueConstraintArtist);
            createOrReplaceFunction(functionNameSong, tableNameArtist, columnNameSong, referencedTableSong, referenceIdColumnSong);
            dropAndCreateTrigger(tableNameArtist, triggerNameSong, functionNameSong);

            createTableIfNotExists(tableNameAlbumArtist, columnNameAlbumArtist, columnTypeAlbumArtist);
            addColumnIfNotExists(tableNameAlbumArtist, columnNameAlbumArtist, columnTypeAlbumArtist);
            addUniqueConstraint(tableNameAlbumArtist, uniqueConstraintAlbumArtist);
            createOrReplaceFunction(functionNameAlbumArtist, tableNameAlbumArtist, columnNameAlbumArtist, referencedTableAlbumArtist, referenceIdColumnAlbumArtist);
            dropAndCreateTrigger(tableNameAlbumArtist, triggerNameAlbumArtist, functionNameAlbumArtist);
            
            createTableIfNotExists(tableNameFeatureArtist, columnNameAlbumArtist, columnTypeAlbumArtist);
            addColumnIfNotExists(tableNameFeatureArtist, columnNameAlbumArtist, columnTypeAlbumArtist);
            addUniqueConstraint(tableNameFeatureArtist, uniqueConstraintAlbumArtist);
            createOrReplaceFunction(functionNameAlbumArtist, tableNameFeatureArtist, columnNameAlbumArtist, referencedTableAlbumArtist, referenceIdColumnAlbumArtist);
            dropAndCreateTrigger(tableNameFeatureArtist, triggerNameAlbumArtist, functionNameAlbumArtist);

            System.out.println("Database initialization completed.");
        } catch (Exception e) {
            System.err.println("Error initializing database...");
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists(String tableName, String columnName, String columnType) {
        jdbcTemplate.execute(String.format(
            "CREATE TABLE IF NOT EXISTS %s (artist_id BIGINT NOT NULL, song_id BIGINT NOT NULL, %s %s);",
            tableName, columnName, columnType
        ));
    }

    private void addColumnIfNotExists(String tableName, String columnName, String columnType) {
        jdbcTemplate.execute(String.format(
            "DO $$ " +
            "BEGIN " +
            "IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='%s' AND column_name='%s') THEN " +
            "ALTER TABLE %s ADD COLUMN %s %s; " +
            "END IF; " +
            "END $$;",
            tableName, columnName, tableName, columnName, columnType
        ));
    }

    private void addUniqueConstraint(String tableName, String uniqueConstraint) {
        jdbcTemplate.execute(String.format("ALTER TABLE %s ADD UNIQUE (%s);", tableName, uniqueConstraint));
    }

    private void createOrReplaceFunction(String functionName, String tableName, String columnName, 
                                         String referencedTable, String referenceIdColumn) {
        jdbcTemplate.execute(String.format(
            "CREATE OR REPLACE FUNCTION %s() " +
            "RETURNS TRIGGER AS $$ " +
            "BEGIN " +
            "NEW.%s := (SELECT %s FROM %s WHERE %s = NEW.%s); " +
            "RETURN NEW; " +
            "END; " +
            "$$ LANGUAGE plpgsql;",
            functionName, columnName, columnName, referencedTable, referenceIdColumn, referenceIdColumn
        ));
    }

    private void dropAndCreateTrigger(String tableName, String triggerName, String functionName) {
        jdbcTemplate.execute(String.format(
            "DROP TRIGGER IF EXISTS %s ON %s; " +
            "CREATE TRIGGER %s " +
            "BEFORE INSERT OR UPDATE ON %s " +
            "FOR EACH ROW " +
            "EXECUTE FUNCTION %s();",
            triggerName, tableName, triggerName, tableName, functionName
        ));
    }


    
}
