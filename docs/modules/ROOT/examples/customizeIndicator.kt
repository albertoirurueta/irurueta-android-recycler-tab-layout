val density = recyclerTabLayout.resources.displayMetrics.density
// ARGB color, for instance ContextCompat.getColor(context, R.color.accent)
recyclerTabLayout.indicatorColor = Color.RED
recyclerTabLayout.indicatorHeight = (3 * density).toInt()
recyclerTabLayout.indicatorMarginLeft = (8 * density).toInt()
recyclerTabLayout.indicatorMarginRight = (8 * density).toInt()
recyclerTabLayout.invalidate()
