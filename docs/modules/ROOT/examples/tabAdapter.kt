class TabViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

class TabAdapter(
    private val titles: List<String>,
    private val onTabClick: (position: Int) -> Unit
) : RecyclerView.Adapter<TabViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        val density = parent.resources.displayMetrics.density
        val textView = TextView(parent.context)
        textView.gravity = Gravity.CENTER
        textView.minWidth = (72 * density).toInt()
        textView.layoutParams = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return TabViewHolder(textView)
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        holder.textView.text = titles[position]
        holder.textView.setOnClickListener {
            val clickedPosition = holder.bindingAdapterPosition
            if (clickedPosition != RecyclerView.NO_POSITION) {
                onTabClick(clickedPosition)
            }
        }
    }

    override fun getItemCount(): Int = titles.size
}
