package com.navneet.foodgenie.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.navneet.foodgenie.R

class FAQFragment : Fragment() {
    lateinit var ques1: TextView
    lateinit var ques2: TextView
    lateinit var ques3: TextView
    lateinit var ques4: TextView
    lateinit var ques5: TextView
    lateinit var ans1: TextView
    lateinit var ans2: TextView
    lateinit var ans3: TextView
    lateinit var ans4: TextView
    lateinit var ans5: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_f_a_q, container, false)
        ques1 = view.findViewById(R.id.ques0)
        ques2 = view.findViewById(R.id.ques1)
        ques3 = view.findViewById(R.id.ques2)
        ques4 = view.findViewById(R.id.ques3)
        ques5 = view.findViewById(R.id.ques4)
        ans1 = view.findViewById(R.id.ans0)
        ans2 = view.findViewById(R.id.ans1)
        ans3 = view.findViewById(R.id.ans2)
        ans4 = view.findViewById(R.id.ans3)
        ans5 = view.findViewById(R.id.ans4)
        ans1.visibility = View.VISIBLE
        ques1.setOnClickListener {
            ans1.visibility = View.VISIBLE
            ans2.visibility = View.GONE
            ans3.visibility = View.GONE
            ans4.visibility = View.GONE
            ans5.visibility = View.GONE
        }
        ques2.setOnClickListener {
            ans1.visibility = View.GONE
            ans2.visibility = View.VISIBLE
            ans3.visibility = View.GONE
            ans4.visibility = View.GONE
            ans5.visibility = View.GONE
        }
        ques3.setOnClickListener {
            ans1.visibility = View.GONE
            ans2.visibility = View.GONE
            ans3.visibility = View.VISIBLE
            ans4.visibility = View.GONE
            ans5.visibility = View.GONE
        }
        ques4.setOnClickListener {
            ans1.visibility = View.GONE
            ans2.visibility = View.GONE
            ans3.visibility = View.GONE
            ans4.visibility = View.VISIBLE
            ans5.visibility = View.GONE
        }
        ques5.setOnClickListener {
            ans1.visibility = View.GONE
            ans2.visibility = View.GONE
            ans3.visibility = View.GONE
            ans4.visibility = View.GONE
            ans5.visibility = View.VISIBLE
        }
        return view
    }

}