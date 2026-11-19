// selecting a page selects the matching tab
viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
    override fun onPageSelected(position: Int) {
        recyclerTabLayout.setCurrentItem(position)
    }
})

// clicking a tab shows the matching page
recyclerTabLayout.adapter = TabAdapter(titles) { position ->
    viewPager.currentItem = position
}
