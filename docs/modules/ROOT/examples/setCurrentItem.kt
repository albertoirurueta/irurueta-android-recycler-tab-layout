// scroll to tab 5 and animate the indicator towards it
recyclerTabLayout.setCurrentItem(5)

// select tab 5 immediately, without animation (for instance when restoring state)
recyclerTabLayout.setCurrentItem(5, smoothScroll = false)
