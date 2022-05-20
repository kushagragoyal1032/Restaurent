package fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.kushagra.restaurent.R


class ProfileFragment : Fragment()
{

    lateinit var ProfileName : TextView
    lateinit var ProfileMob : TextView
    lateinit var ProfileEmail : TextView
    lateinit var ProfileAddress : TextView
    lateinit var sharedPreferences_for_profile: SharedPreferences

    var uname : String? = null
    var email : String? = null
    var mobile : String? = null
    var address : String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        ProfileName = view.findViewById(R.id.ProfileName)
        ProfileMob = view.findViewById(R.id.ProfileMob)
        ProfileEmail = view.findViewById(R.id.ProfileEmail)
        ProfileAddress = view.findViewById(R.id.ProfileAddress)

        sharedPreferences_for_profile = requireContext().getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

         uname = sharedPreferences_for_profile.getString("name1", "defaultdata")
         email = sharedPreferences_for_profile.getString("email", "defaultdata")
        mobile = sharedPreferences_for_profile.getString("number", "defaultdata")
        address = sharedPreferences_for_profile.getString("address", "defaultdata")

        ProfileName.text = uname
        ProfileEmail.text = email
        ProfileMob.text = mobile
        ProfileAddress.text = address

        return view
    }


}