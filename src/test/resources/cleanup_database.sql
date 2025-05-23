DO
'
DECLARE
    r RECORD;
BEGIN
    FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = ''public'' AND tablename NOT IN (''flyway_schema_history'', ''alembic_version'', ''caveat'', ''metadata'', ''namespace_config'', ''relation_tuple'', ''relation_tuple_transaction'', ''relationship_counter''))
        LOOP
            EXECUTE ''TRUNCATE TABLE '' || quote_ident(r.tablename) || '' CASCADE'';
        END LOOP;
END;

' LANGUAGE plpgsql;
