// 10,000 tabs: only the visible tab views are created, and they are recycled on scroll
val titles = List(10_000) { "Tab $it" }
recyclerTabLayout.adapter = TabAdapter(titles) { position ->
    recyclerTabLayout.setCurrentItem(position)
}
