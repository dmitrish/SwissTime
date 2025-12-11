package com.coroutines.swisstime.util

import android.util.Log

/**
 * Utility class for collecting and reporting performance metrics. This class provides methods for
 * tracking various performance metrics such as page transitions, rendering times, and other
 * performance-related data.
 */
object PerformanceMetrics {
  private const val TAG = "PerformanceMetrics"

  // Map to store metrics by category
  private val metrics = mutableMapOf<String, MutableList<MetricEntry>>()

  /**
   * Records a metric with the given category, name, and duration.
   *
   * @param category The category of the metric (e.g., "PageTransition", "PageRendering")
   * @param name The name of the metric (e.g., "Page0to1", "Page1Rendering")
   * @param durationMs The duration of the metric in milliseconds
   * @param metadata Additional metadata to associate with the metric
   */
  fun recordMetric(
    category: String,
    name: String,
    durationMs: Long,
    metadata: Map<String, Any> = emptyMap()
  ) {
    val entry =
      MetricEntry(
        timestamp = System.currentTimeMillis(),
        name = name,
        durationMs = durationMs,
        metadata = metadata
      )

    synchronized(metrics) {
      val categoryMetrics = metrics.getOrPut(category) { mutableListOf() }
      categoryMetrics.add(entry)
    }

    // Log the metric
    Log.d(TAG, "[$category] $name: $durationMs ms ${formatMetadata(metadata)}")
  }

  /**
   * Gets all metrics for a given category.
   *
   * @param category The category of metrics to retrieve
   * @return A list of metric entries for the given category
   */
  fun getMetrics(category: String): List<MetricEntry> {
    synchronized(metrics) {
      return metrics[category]?.toList() ?: emptyList()
    }
  }

  /**
   * Gets the average duration of metrics for a given category.
   *
   * @param category The category of metrics to analyze
   * @return The average duration in milliseconds, or 0 if no metrics exist
   */
  fun getAverageDuration(category: String): Double {
    val categoryMetrics = getMetrics(category)
    if (categoryMetrics.isEmpty()) return 0.0

    val totalDuration = categoryMetrics.sumOf { it.durationMs }
    return totalDuration.toDouble() / categoryMetrics.size
  }

  /** Clears all metrics. */
  fun clearMetrics() {
    synchronized(metrics) { metrics.clear() }
  }

  /**
   * Clears metrics for a specific category.
   *
   * @param category The category of metrics to clear
   */
  fun clearMetrics(category: String) {
    synchronized(metrics) { metrics.remove(category) }
  }

  /** Formats metadata as a string for logging. */
  private fun formatMetadata(metadata: Map<String, Any>): String {
    if (metadata.isEmpty()) return ""

    return metadata.entries.joinToString(prefix = "[", postfix = "]") { (key, value) ->
      "$key: $value"
    }
  }

  /** Data class representing a single metric entry. */
  data class MetricEntry(
    val timestamp: Long,
    val name: String,
    val durationMs: Long,
    val metadata: Map<String, Any> = emptyMap()
  )

  /** Constants for metric categories. */
  object Categories {
    const val PAGE_TRANSITION = "PageTransition"
    const val PAGE_RENDERING = "PageRendering"
  }
}
