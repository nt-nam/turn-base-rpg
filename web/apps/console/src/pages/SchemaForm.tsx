export interface Schema {
  type?: "string" | "integer" | "number" | "boolean" | "array" | "object" | "null";
  enum?: string[];
  properties?: Record<string, Schema>;
  required?: string[];
  items?: Schema;
  additionalProperties?: Schema | boolean;
  anyOf?: Schema[];
  title?: string;
}

type Value = unknown;

export function unwrap(schema: Schema): { schema: Schema; nullable: boolean } {
  if (!schema.anyOf) return { schema, nullable: false };
  const concrete = schema.anyOf.find((option) => option.type !== "null") ?? {};
  return { schema: concrete, nullable: schema.anyOf.some((option) => option.type === "null") };
}

export function defaultFor(input: Schema): Value {
  const { schema, nullable } = unwrap(input);
  if (nullable) return null;
  if (schema.enum) return schema.enum[0];
  switch (schema.type) {
    case "string":
      return "";
    case "integer":
    case "number":
      return 0;
    case "boolean":
      return false;
    case "array":
      return [];
    case "object":
      if (schema.properties) {
        return Object.fromEntries((schema.required ?? []).map((key) => [key, defaultFor(schema.properties![key])]));
      }
      return {};
    default:
      return null;
  }
}

const isRecord = (value: Value): value is Record<string, Value> => typeof value === "object" && value !== null && !Array.isArray(value);

export function SchemaField({ schema: input, value, onChange, path }: { schema: Schema; value: Value; onChange: (next: Value) => void; path: string }) {
  const { schema, nullable } = unwrap(input);
  const nullToggle = nullable && (
    <label className="check inline">
      <input type="checkbox" checked={value !== null && value !== undefined} onChange={(event) => onChange(event.target.checked ? defaultFor(schema) : null)} />
      có giá trị
    </label>
  );
  if (nullable && (value === null || value === undefined)) return <div className="field-row">{nullToggle}</div>;

  if (schema.enum) {
    return (
      <div className="field-row">
        <select value={String(value ?? "")} onChange={(event) => onChange(event.target.value)} aria-label={path}>
          {schema.enum.map((option) => (
            <option key={option} value={option}>
              {option}
            </option>
          ))}
        </select>
        {nullToggle}
      </div>
    );
  }

  switch (schema.type) {
    case "string":
      return (
        <div className="field-row">
          <input value={String(value ?? "")} onChange={(event) => onChange(event.target.value)} aria-label={path} />
          {nullToggle}
        </div>
      );
    case "integer":
    case "number":
      return (
        <div className="field-row">
          <input
            type="number"
            step={schema.type === "integer" ? 1 : "any"}
            value={typeof value === "number" ? value : 0}
            onChange={(event) => onChange(schema.type === "integer" ? Math.trunc(Number(event.target.value)) : Number(event.target.value))}
            aria-label={path}
          />
          {nullToggle}
        </div>
      );
    case "boolean":
      return (
        <label className="check">
          <input type="checkbox" checked={value === true} onChange={(event) => onChange(event.target.checked)} aria-label={path} />
          {value === true ? "có" : "không"}
        </label>
      );
    case "array":
      return <ArrayField schema={schema.items ?? {}} value={Array.isArray(value) ? value : []} onChange={onChange} path={path} />;
    case "object":
      if (schema.properties) return <ObjectField schema={schema} value={isRecord(value) ? value : {}} onChange={onChange} path={path} />;
      return <MapField schema={typeof schema.additionalProperties === "object" ? schema.additionalProperties : {}} value={isRecord(value) ? value : {}} onChange={onChange} path={path} />;
    default:
      return <code>{JSON.stringify(value)}</code>;
  }
}

export function ObjectField({ schema, value, onChange, path }: { schema: Schema; value: Record<string, Value>; onChange: (next: Value) => void; path: string }) {
  const required = new Set(schema.required ?? []);
  return (
    <div className="object-field">
      {Object.entries(schema.properties ?? {}).map(([key, property]) => {
        const present = key in value;
        const set = (next: Value) => onChange({ ...value, [key]: next });
        const remove = () => {
          const { [key]: _removed, ...rest } = value;
          onChange(rest);
        };
        return (
          <div className="object-row" key={key}>
            <div className="object-key">
              <code>{key}</code>
              {!required.has(key) && (
                <label className="check inline">
                  <input type="checkbox" checked={present} onChange={(event) => (event.target.checked ? set(defaultFor(property)) : remove())} />
                  tuỳ chọn
                </label>
              )}
            </div>
            <div className="object-value">{(present || required.has(key)) && <SchemaField schema={property} value={value[key]} onChange={set} path={`${path}.${key}`} />}</div>
          </div>
        );
      })}
    </div>
  );
}

function ArrayField({ schema, value, onChange, path }: { schema: Schema; value: Value[]; onChange: (next: Value) => void; path: string }) {
  return (
    <div className="array-field">
      {value.map((item, index) => (
        <div className="array-item" key={index}>
          <SchemaField schema={schema} value={item} onChange={(next) => onChange(value.map((existing, position) => (position === index ? next : existing)))} path={`${path}[${index}]`} />
          <button type="button" className="icon" aria-label={`Xoá ${path}[${index}]`} onClick={() => onChange(value.filter((_, position) => position !== index))}>
            ×
          </button>
        </div>
      ))}
      <button type="button" onClick={() => onChange([...value, defaultFor(schema)])}>
        + Thêm
      </button>
    </div>
  );
}

function MapField({ schema, value, onChange, path }: { schema: Schema; value: Record<string, Value>; onChange: (next: Value) => void; path: string }) {
  const entries = Object.entries(value);
  return (
    <div className="array-field">
      {entries.map(([key, item]) => (
        <div className="array-item" key={key}>
          <input
            className="map-key"
            defaultValue={key}
            aria-label={`${path} khoá`}
            onBlur={(event) => {
              const renamed = event.target.value.trim();
              if (!renamed || renamed === key || renamed in value) return;
              onChange(Object.fromEntries(entries.map(([existing, content]) => (existing === key ? [renamed, content] : [existing, content]))));
            }}
          />
          <SchemaField schema={schema} value={item} onChange={(next) => onChange({ ...value, [key]: next })} path={`${path}.${key}`} />
          <button type="button" className="icon" aria-label={`Xoá ${key}`} onClick={() => onChange(Object.fromEntries(entries.filter(([existing]) => existing !== key)))}>
            ×
          </button>
        </div>
      ))}
      <button type="button" onClick={() => onChange({ ...value, [`key${entries.length + 1}`]: defaultFor(schema) })}>
        + Thêm khoá
      </button>
    </div>
  );
}
