CREATE INDEX telemetry_events_created_at ON telemetry_events (created_at, id);
CREATE INDEX telemetry_events_name_created_at ON telemetry_events (name, created_at);
CREATE INDEX telemetry_events_account_created_at ON telemetry_events (account_id, created_at);
DROP INDEX IF EXISTS telemetry_events_name;
