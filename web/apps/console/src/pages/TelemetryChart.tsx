import { useEffect, useRef, useState, type KeyboardEvent, type PointerEvent } from "react";
import type { TelemetryBucket, TelemetrySummary } from "../api";

const COLORED_SLOTS = 4;
const HEIGHT = 240;
const TOP = 24;
const BOTTOM = 26;
const RIGHT = 8;
const MAX_BAR = 24;
const SEGMENT_GAP = 2;
const TICK_SPACING = 84;
const DAY = 24 * 60 * 60 * 1000;
const TICK_STEPS: Record<TelemetryBucket, number[]> = {
  minute: [1, 5, 10, 15, 30, 60, 120, 180, 360, 720],
  hour: [1, 2, 3, 6, 12, 24, 48, 168, 336],
  day: [1, 2, 7, 14, 30],
};
const AGGREGATE = "*";

export interface ChartLayer {
  key: string;
  label: string;
  slot: number | "other";
  counts: number[];
  total: number;
}

const sum = (values: number[]) => values.reduce((total, value) => total + value, 0);

export const formatCount = (value: number) => value.toLocaleString("vi-VN");

export const seriesClass = (slot: ChartLayer["slot"]) => `chart-series-${slot}`;

export function chartLayers(summary: TelemetrySummary): ChartLayer[] {
  const slots = new Map(summary.availableNames.slice(0, COLORED_SLOTS).map((entry, index) => [entry.name, index]));
  const colored = summary.series
    .flatMap((series) => {
      const slot = slots.get(series.name);
      return slot === undefined ? [] : [{ key: series.name, label: series.name, slot, counts: series.counts, total: sum(series.counts) }];
    })
    .sort((first, second) => first.slot - second.slot);
  const rest = summary.series.filter((series) => !slots.has(series.name));
  if (rest.length === 0) return colored;
  const counts = summary.bucketStarts.map((_, index) => sum(rest.map((series) => series.counts[index])));
  const single = rest.length === 1 ? rest[0].name : undefined;
  return [...colored, { key: single ?? AGGREGATE, label: single ?? `Khác (${rest.length} loại)`, slot: "other", counts, total: sum(counts) }];
}

const pad = (value: number) => String(value).padStart(2, "0");
const localClock = (date: Date) => `${pad(date.getHours())}:${pad(date.getMinutes())}`;
const localDay = (date: Date) => `${pad(date.getDate())}/${pad(date.getMonth() + 1)}`;
const utcDay = (date: Date) => `${pad(date.getUTCDate())}/${pad(date.getUTCMonth() + 1)}`;

export function bucketSpan(start: number, bucket: TelemetryBucket, millis: number) {
  const date = new Date(start);
  if (bucket === "day") return `${utcDay(date)}/${date.getUTCFullYear()} (UTC)`;
  return `${localDay(date)} ${localClock(date)}–${localClock(new Date(start + millis))}`;
}

function tickLabel(start: number, bucket: TelemetryBucket, every: number, spansDays: boolean) {
  const date = new Date(start);
  if (bucket === "day") return utcDay(date);
  if (bucket === "minute" || !spansDays) return localClock(date);
  return every >= 24 ? localDay(date) : `${localDay(date)} ${localClock(date)}`;
}

function alignedIndex(start: number, bucket: TelemetryBucket, millis: number) {
  const local = bucket === "day" ? start : start - new Date(start).getTimezoneOffset() * 60_000;
  return Math.round(local / millis);
}

function valueTicks(maximum: number) {
  if (maximum <= 0) return [0, 1];
  const rough = maximum / 4;
  const magnitude = 10 ** Math.floor(Math.log10(rough));
  const step = Math.max(1, [1, 2, 5, 10].map((factor) => factor * magnitude).find((candidate) => candidate >= rough) ?? rough);
  const top = Math.ceil(maximum / step) * step;
  return Array.from({ length: Math.round(top / step) + 1 }, (_, index) => index * step);
}

function roundedTop(x: number, y: number, width: number, height: number) {
  const radius = Math.min(4, width / 2, height);
  if (radius < 1) return `M${x},${y}h${width}v${height}h${-width}Z`;
  return `M${x},${y + height}V${y + radius}Q${x},${y} ${x + radius},${y}H${x + width - radius}Q${x + width},${y} ${x + width},${y + radius}V${y + height}Z`;
}

function useWidth<T extends HTMLElement>() {
  const frame = useRef<T>(null);
  const [width, setWidth] = useState(0);
  useEffect(() => {
    const node = frame.current;
    if (!node) return;
    const observer = new ResizeObserver(([entry]) => setWidth(Math.floor(entry.contentRect.width)));
    observer.observe(node);
    return () => observer.disconnect();
  }, []);
  return [frame, width] as const;
}

export function TelemetryChart({ summary, layers }: { summary: TelemetrySummary; layers: ChartLayer[] }) {
  const [frame, width] = useWidth<HTMLDivElement>();
  const [active, setActive] = useState<number>();
  const buckets = summary.bucketStarts;
  const columnTotals = buckets.map((_, index) => sum(layers.map((layer) => layer.counts[index])));
  const peak = Math.max(0, ...columnTotals);
  const ticks = valueTicks(peak);
  const tickLabels = ticks.map(formatCount);
  const left = 12 + 7 * Math.max(...tickLabels.map((label) => label.length));
  const plotWidth = Math.max(1, width - left - RIGHT);
  const plotHeight = HEIGHT - TOP - BOTTOM;
  const scaleTop = ticks[ticks.length - 1];
  const y = (value: number) => TOP + plotHeight - (value / scaleTop) * plotHeight;
  const step = plotWidth / buckets.length;
  const barWidth = Math.min(MAX_BAR, Math.max(1, step - (step > 6 ? 2 : step > 3 ? 1 : 0)));
  const center = (index: number) => left + index * step + step / 2;
  const clampLabel = (x: number) => Math.min(Math.max(x, left + 18), width - RIGHT - 18);
  const every = TICK_STEPS[summary.bucket].find((candidate) => buckets.length / candidate <= Math.max(2, Math.floor(plotWidth / TICK_SPACING))) ?? TICK_STEPS[summary.bucket].slice(-1)[0];
  const aligned = buckets.flatMap((start, index) => (alignedIndex(start, summary.bucket, summary.bucketMillis) % every === 0 ? [index] : []));
  const xTicks = aligned.length > 0 ? aligned : [0];
  const spansDays = summary.to - summary.from > DAY;
  const peakIndex = columnTotals.indexOf(peak);
  const below = layers.map((_, layerIndex) => buckets.map((__, index) => sum(layers.slice(0, layerIndex).map((layer) => layer.counts[index]))));

  const locate = (event: PointerEvent<SVGSVGElement>) => {
    const index = Math.floor((event.clientX - event.currentTarget.getBoundingClientRect().left - left) / step);
    setActive(index >= 0 && index < buckets.length ? index : undefined);
  };
  const move = (event: KeyboardEvent<SVGSVGElement>) => {
    const current = active ?? buckets.length - 1;
    const moves: Record<string, number> = { ArrowLeft: current - 1, ArrowRight: current + 1, Home: 0, End: buckets.length - 1 };
    const next = moves[event.key];
    if (next === undefined) return;
    event.preventDefault();
    setActive(Math.min(Math.max(next, 0), buckets.length - 1));
  };

  const tooltipLeft = active !== undefined && center(active) < width / 2;
  return (
    <div className="chart" data-total={summary.total} data-buckets={buckets.length}>
      {layers.length > 1 && (
        <ul className="chart-legend" aria-label="Chú giải">
          {layers.map((layer) => (
            <li key={layer.key}>
              <span className={`swatch ${seriesClass(layer.slot)}`} />
              {layer.label}
            </li>
          ))}
        </ul>
      )}
      <div className="chart-frame" ref={frame}>
        {width > 0 && (
          <svg
            width={width}
            height={HEIGHT}
            role="img"
            tabIndex={0}
            aria-label={`Biểu đồ cột chồng: ${formatCount(summary.total)} sự kiện trong ${buckets.length} cột. Dùng phím mũi tên để xem từng cột.`}
            onPointerMove={locate}
            onPointerLeave={() => setActive(undefined)}
            onKeyDown={move}
            onFocus={() => setActive((current) => current ?? Math.max(0, peakIndex))}
            onBlur={() => setActive(undefined)}
          >
            {active !== undefined && <rect className="chart-hover" x={left + active * step} y={TOP} width={step} height={plotHeight} />}
            {ticks.map((value, index) => (
              <g key={value}>
                <line className="chart-grid" x1={left} x2={width - RIGHT} y1={y(value)} y2={y(value)} />
                <text className="chart-axis" x={left - 6} y={y(value)} dy="0.32em" textAnchor="end">
                  {tickLabels[index]}
                </text>
              </g>
            ))}
            {layers.map((layer, layerIndex) => (
              <g key={layer.key} className={seriesClass(layer.slot)} data-series={layer.key} data-count={layer.total}>
                {layer.counts.map((count, index) => {
                  if (count === 0) return null;
                  const base = below[layerIndex][index];
                  const top = y(base + count);
                  const height = y(base) - top;
                  const x = center(index) - barWidth / 2;
                  const covered = layers.slice(layerIndex + 1).some((upper) => upper.counts[index] > 0);
                  if (!covered) return <path key={index} d={roundedTop(x, top, barWidth, height)} />;
                  const gap = height > SEGMENT_GAP + 1 ? SEGMENT_GAP : 0;
                  return <rect key={index} x={x} y={top + gap} width={barWidth} height={height - gap} />;
                })}
              </g>
            ))}
            {xTicks.map((index) => (
              <text key={index} className="chart-axis" x={clampLabel(center(index))} y={HEIGHT - 8} textAnchor="middle">
                {tickLabel(buckets[index], summary.bucket, every, spansDays)}
              </text>
            ))}
            {peak > 0 && (
              <text className="chart-value" x={clampLabel(center(peakIndex))} y={y(peak) - 6} textAnchor="middle">
                {formatCount(peak)}
              </text>
            )}
          </svg>
        )}
        {active !== undefined && (
          <div className="chart-tooltip" style={tooltipLeft ? { left: center(active) + 12 } : { right: width - center(active) + 12 }}>
            <div className="muted">{bucketSpan(buckets[active], summary.bucket, summary.bucketMillis)}</div>
            <div>
              <strong>{formatCount(columnTotals[active])}</strong> <span className="muted">sự kiện</span>
            </div>
            {layers.length > 1 && (
              <ul>
                {[...layers].reverse().map((layer) => (
                  <li key={layer.key}>
                    <span className={`line-key ${seriesClass(layer.slot)}`} />
                    <strong>{formatCount(layer.counts[active])}</strong> <span className="muted">{layer.label}</span>
                  </li>
                ))}
              </ul>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
