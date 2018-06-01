package sandra.app.hr.weatherapp.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import sandra.app.hr.weatherapp.R
import sandra.app.hr.weatherapp.databinding.ForecastItemBinding
import sandra.app.hr.weatherapp.model.ForecastItem
import sandra.app.hr.weatherapp.net.imageUrl

class RecyclerViewForecastAdapter(private val context: Context,
                                  private val list: List<ForecastItem>
) : RecyclerView.Adapter<ForecastViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ForecastItemBinding.inflate(layoutInflater, parent, false)
        return ForecastViewHolder(context, binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(list[position])
    }
}

class ForecastViewHolder(private val context: Context,
                         private val binding: ForecastItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: ForecastItem) {
        binding.apply {
            Picasso.get().load(imageUrl(data.weather.first().icon)).into(ivForecastIcon)
            tvDay.text = DateTime.parse(data.timestamp, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
                    .dayOfWeek()
                    .asText
            tvForecastTempHigh.text = context.getString(R.string.deg, data.main.tempMax)
            tvForecastTempLow.text = context.getString(R.string.deg, data.main.tempMin)
            executePendingBindings()
        }
    }
}
