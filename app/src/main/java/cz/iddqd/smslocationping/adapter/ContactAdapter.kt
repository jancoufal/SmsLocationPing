package cz.iddqd.smslocationping.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.iddqd.smslocationping.R
import cz.iddqd.smslocationping.model.Contact

class ContactAdapter(private val context: Context, private val contactDataset: List<Contact>)
	: RecyclerView.Adapter<ContactAdapter.ItemViewHolder>()
{
	class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
		val textView: TextView = view.findViewById(R.id.textContactItem)
		val buttonContactDelete: ImageButton = view.findViewById(R.id.buttonContactDelete)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
		val adapterLayout = LayoutInflater.from(parent.context)
			.inflate(R.layout.contact_item, parent, false)

		return ItemViewHolder(adapterLayout)
	}

	override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
		val item = contactDataset[position]

		"${item.contactName}: ${item.contactNumber}".also { holder.textView.text = it }
		holder.buttonContactDelete.setOnClickListener(this@ContactAdapter::onClick)
	}

	override fun getItemCount(): Int = contactDataset.size

	private fun onClick(v: View?) {
		Log.d("EVENT", "$v")
	}
}