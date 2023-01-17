package com.bigjeon.johoblite.analysis

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bigjeon.johoblite.service.OcrFactory
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.data.DroneImageItem
import com.bigjeon.johoblite.databinding.FragmentStitchingInspectBinding
import com.bigjeon.johoblite.inter.CheckBoxInterface
import com.bigjeon.johoblite.library.ApiClient
import com.bigjeon.johoblite.rcvadapter.StitchingDroneImageRcvAdapter
import com.bigjeon.johoblite.viewmodel.MainViewModel
import com.bumptech.glide.Glide

class StitchingInspectFragment : Fragment(), CheckBoxInterface{
	
	private lateinit var binding: FragmentStitchingInspectBinding
	private val model by activityViewModels<MainViewModel>()
	//retrofit2
	private lateinit var serviceClient: OcrFactory
	private lateinit var droneImageRcvAdapter: StitchingDroneImageRcvAdapter

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentStitchingInspectBinding.inflate(inflater, container, false)

		val activity = requireActivity()

		serviceClient = ApiClient.retrofit.create(OcrFactory::class.java)

		initDroneImageRcv()

		binding.stitchingPreviewImageView.maximumScale = binding.stitchingPreviewImageView.maximumScale * 2

		model.planeSectionState.observe(viewLifecycleOwner){ source ->
			Log.d("STITCHING", "STIT")
			if (source.code != "NONE"){
				binding.droneImageNameTv.text = "이미지를 선택해주세요."
				binding.droneImageUploaderTv.text = "-"
				binding.droneImageDateTv.text = "-"
				Glide.with(activity).load(0).into(binding.stitchingPreviewImageView)
				droneImageRcvAdapter.clearImageList()
				if (activity != null && isAdded) {
					getStitchedImage()
				}
			}else{
				binding.selectedImageSizeTv.text = "0"
				binding.selectedImageCountTv.text = "0"
				binding.selectAllCheckBox.isChecked = false
			}
		}
		model.stitchedImage.observe(activity){ source ->
			val circularProgressDrawable = CircularProgressDrawable(activity)
			circularProgressDrawable.strokeWidth = 5f
			circularProgressDrawable.centerRadius = 30f
			circularProgressDrawable.start()
			/*if (source != "NONE"){
				binding.noneImageTV.visibility = View.GONE
				Glide.with(activity).load(source).placeholder(circularProgressDrawable).into(binding.stitchingPreviewImageView)
			}else */if(source == "인천"){
				Glide.with(activity).load(R.drawable.paldang).placeholder(circularProgressDrawable).into(binding.stitchingPreviewImageView)
			}else if(source == "평택"){
				Glide.with(activity).load(R.drawable.seongnam).placeholder(circularProgressDrawable).into(binding.stitchingPreviewImageView)
			}
		}
		binding.selectAllCheckBox.setOnClickListener {
			if (binding.selectAllCheckBox.isChecked) {
				droneImageRcvAdapter.setSelect(true)
			} else {
				droneImageRcvAdapter.setSelect(false)
			}
		}
		return binding.root
	}
	
	private fun initDroneImageRcv(){
		droneImageRcvAdapter = StitchingDroneImageRcvAdapter(requireContext(), this)
		val lm = LinearLayoutManager(requireContext()).also { it.orientation = LinearLayoutManager.VERTICAL }
		binding.uploadImageRcv.adapter = droneImageRcvAdapter
		binding.uploadImageRcv.setHasFixedSize(false)
		binding.uploadImageRcv.layoutManager = lm
		droneImageRcvAdapter.setOnItemClickListener(object : StitchingDroneImageRcvAdapter.OnItemClickListener{
			override fun onItemClick(v: View, data: DroneImageItem, uri: Int, position: Int) {
				binding.droneImageNameTv.text = data.fileName
				binding.droneImageDateTv.text = data.date
				binding.droneImageUploaderTv.text = data.uploader
				val circularProgressDrawable = CircularProgressDrawable(requireContext())
				circularProgressDrawable.strokeWidth = 5f
				circularProgressDrawable.centerRadius = 30f
				circularProgressDrawable.start()
				Glide.with(requireContext()).load(uri).placeholder(circularProgressDrawable).into(binding.droneImageView)
				droneImageRcvAdapter.changeShowingPosition(position)
			}
			
		})
	}
	
	private fun getStitchedImage(){
		Log.d("getDroneImageList", "getDroneImageList로드")
		if (model.directionState.value?.Name == "인천계통"){
			model.setStitchedImage("인천")
			/*droneImageRcvAdapter.addImageList(DroneImageItem(
				i.asJsonObject.get("fileName").asString,
				i.asJsonObject.get("imagePath").asString,
				i.asJsonObject.get("uploader").asString,
				i.asJsonObject.get("date").asString,
				i.asJsonObject.get("note").asString"-",
				false
			))*/
		}else if (model.directionState.value?.Name == "평택계통"){
			model.setStitchedImage("평택")
			/*droneImageRcvAdapter.addImageList(DroneImageItem(
				i.asJsonObject.get("fileName").asString,
				i.asJsonObject.get("imagePath").asString,
				i.asJsonObject.get("uploader").asString,
				i.asJsonObject.get("date").asString,
				i.asJsonObject.get("note").asString"-",
				false
			))*/
		}
		if (model.directionState.value?.Name == "인천계통"){
			for(i in 169 .. 205){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang1_max_0" + i,
					resources.getIdentifier("paldang1_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 206 .. 241){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang2_max_0" + i,
					resources.getIdentifier("paldang2_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 242 .. 276){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang3_max_0" + i,
					resources.getIdentifier("paldang3_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 277 .. 319){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang4_max_0" + i,
					resources.getIdentifier("paldang4_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 320 .. 362){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang5_max_0" + i,
					resources.getIdentifier("paldang5_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 363 .. 403){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang6_max_0" + i,
					resources.getIdentifier("paldang6_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 404 .. 441){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang7_max_0" + i,
					resources.getIdentifier("paldang7_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 442 .. 477){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang8_max_0" + i,
					resources.getIdentifier("paldang8_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 478 .. 512){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang9_max_0" + i,
					resources.getIdentifier("paldang9_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 513 .. 551){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang10_max_0" + i,
					resources.getIdentifier("paldang10_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 559 .. 594){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang11_max_0" + i,
					resources.getIdentifier("paldang11_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 552 .. 627){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang12_max_0" + i,
					resources.getIdentifier("paldang12_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 628 .. 659){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang13_max_0" + i,
					resources.getIdentifier("paldang13_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 660 .. 692){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang14_max_0" + i,
					resources.getIdentifier("paldang14_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 693 .. 742){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang15_max_0" + i,
					resources.getIdentifier("paldang15_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 743 .. 785){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang16_max_0" + i,
					resources.getIdentifier("paldang16_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 786 .. 821){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang17_max_0" + i,
					resources.getIdentifier("paldang17_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 822 .. 874){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang18_max_0" + i,
					resources.getIdentifier("paldang18_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 694 .. 721){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang19_max_0" + i,
					resources.getIdentifier("paldang19_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 615 .. 656){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang20_max_0" + i,
					resources.getIdentifier("paldang20_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 580 .. 614){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang21_max_0" + i,
					resources.getIdentifier("paldang21_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 547 .. 579){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang22_max_0" + i,
					resources.getIdentifier("paldang22_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 511 .. 546){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang23_max_0" + i,
					resources.getIdentifier("paldang23_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 475 .. 510){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang24_max_0" + i,
					resources.getIdentifier("paldang24_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 433 .. 474){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang25_max_0" + i,
					resources.getIdentifier("paldang25_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 408 .. 432){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang26_max_0" + i,
					resources.getIdentifier("paldang26_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 370 .. 407){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang27_max_0" + i,
					resources.getIdentifier("paldang27_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 344 .. 369){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang28_max_0" + i,
					resources.getIdentifier("paldang28_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 318 .. 343){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang29_max_0" + i,
					resources.getIdentifier("paldang29_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 292 .. 317){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang30_max_0" + i,
					resources.getIdentifier("paldang30_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 269 .. 291){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang31_max_0" + i,
					resources.getIdentifier("paldang31_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 248 .. 268){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang32_max_0" + i,
					resources.getIdentifier("paldang32_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 223 .. 247){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang33_max_0" + i,
					resources.getIdentifier("paldang33_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 203 .. 222){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang34_max_0" + i,
					resources.getIdentifier("paldang34_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 182 .. 202){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang35_max_0" + i,
					resources.getIdentifier("paldang35_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 156 .. 181){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"paldang36_max_0" + i,
					resources.getIdentifier("paldang36_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			binding.selectAllCheckBox.isChecked = false
			binding.selectedImageSizeTv.text = droneImageRcvAdapter.itemCount.toString()
			binding.selectedImageCountTv.text = "0"
		}else if (model.directionState.value?.Name == "평택계통"){
			for(i in 52 .. 54){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam1_max_00" + i,
					resources.getIdentifier("seongnam1_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 55 .. 57){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam2_max_00" + i,
					resources.getIdentifier("seongnam2_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 58 .. 61){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam3_max_00" + i,
					resources.getIdentifier("seongnam3_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 62 .. 67){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam4_max_00" + i,
					resources.getIdentifier("seongnam4_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 68 .. 71){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam5_max_00" + i,
					resources.getIdentifier("seongnam5_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 72 .. 75){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam6_max_00" + i,
					resources.getIdentifier("seongnam6_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 76 .. 78){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam7_max_00" + i,
					resources.getIdentifier("seongnam7_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 79 .. 81){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam8_max_00" + i,
					resources.getIdentifier("seongnam8_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 82 .. 84){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam9_max_00" + i,
					resources.getIdentifier("seongnam9_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 85 .. 87){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam10_max_00" + i,
					resources.getIdentifier("seongnam10_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 88 .. 91){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam11_max_00" + i,
					resources.getIdentifier("seongnam11_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 92 .. 95){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam12_max_00" + i,
					resources.getIdentifier("seongnam12_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 96 .. 99){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam13_max_00" + i,
					resources.getIdentifier("seongnam13_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			droneImageRcvAdapter.addImageList(DroneImageItem(
				"seongnam14_max_0099",
				resources.getIdentifier("seongnam14_max_0099", "drawable", requireActivity().packageName),
				"komapper",
				"2022-11-18",
				"",
				false
			))
			for(i in 100 .. 102){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam14_max_0" + i,
					resources.getIdentifier("seongnam14_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 103 .. 105){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam15_max_0" + i,
					resources.getIdentifier("seongnam15_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 103 .. 108){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam16_max_0" + i,
					resources.getIdentifier("seongnam16_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 109 .. 112){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam17_max_0" + i,
					resources.getIdentifier("seongnam17_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 113 .. 116){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam18_max_0" + i,
					resources.getIdentifier("seongnam18_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 117 .. 120){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam19_max_0" + i,
					resources.getIdentifier("seongnam19_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 121 .. 123){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam20_max_0" + i,
					resources.getIdentifier("seongnam20_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 124 .. 127){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam21_max_0" + i,
					resources.getIdentifier("seongnam21_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 94 .. 98){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam22_max_00" + i,
					resources.getIdentifier("seongnam22_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			droneImageRcvAdapter.addImageList(DroneImageItem(
				"seongnam23_max_0099",
				resources.getIdentifier("seongnam23_max_0099", "drawable", requireActivity().packageName),
				"komapper",
				"2022-11-18",
				"",
				false
			))
			for(i in 100 .. 101){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam23_max_0" + i,
					resources.getIdentifier("seongnam23_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 102 .. 104){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam24_max_0" + i,
					resources.getIdentifier("seongnam24_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 105 .. 108){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam25_max_0" + i,
					resources.getIdentifier("seongnam25_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 109 .. 116){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam26_max_0" + i,
					resources.getIdentifier("seongnam26_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 117 .. 120){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam27_max_0" + i,
					resources.getIdentifier("seongnam27_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 121 .. 124){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam28_max_0" + i,
					resources.getIdentifier("seongnam28_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 125 .. 129){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam29_max_0" + i,
					resources.getIdentifier("seongnam29_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 130 .. 132){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam30_max_0" + i,
					resources.getIdentifier("seongnam30_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 133 .. 135){
				droneImageRcvAdapter.addImageList(DroneImageItem(
					"seongnam31_max_0" + i,
					resources.getIdentifier("seongnam31_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			binding.selectAllCheckBox.isChecked = false
			binding.selectedImageSizeTv.text = droneImageRcvAdapter.itemCount.toString()
			binding.selectedImageCountTv.text = "0"
		}
		/*CoroutineScope(Dispatchers.IO).launch {
			serviceClient.getCstStitchedImage(model.facilityState.value!!.Code + File.separator + model.directionState.value!!.Code + File.separator + model.keyPlanState.value + File.separator + model.planeState.value + File.separator + model.planeSectionState.value!!.code, "all" +
					"").enqueue(object : retrofit2.Callback<JsonObject> {
				override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
					if (response.isSuccessful && context != null && response.body()!!.get("result").asString.equals("true")) {
						model.setStitchedImage(getString(R.string.URL) + "data/" + response.body()!!.getAsJsonObject("stitchedImageInfo").asJsonObject.getAsJsonPrimitive("path").asString)
						for (i in response.body()!!.getAsJsonArray("droneImageList").asJsonArray){
							droneImageRcvAdapter.addImageList(DroneImageItem(
								i.asJsonObject.get("fileName").asString,
								i.asJsonObject.get("imagePath").asString,
								i.asJsonObject.get("uploader").asString,
								i.asJsonObject.get("date").asString,
								i.asJsonObject.get("note").asString"-",
								false
							))
						}
						binding.selectedImageSizeTv.text = droneImageRcvAdapter.itemCount.toString()
						binding.selectedImageCountTv.text = "0"
					} else {
						binding.selectedImageSizeTv.text = "0"
						binding.selectedImageCountTv.text = "0"
						binding.selectAllCheckBox.isChecked = false
						binding.noneImageTV.visibility = View.VISIBLE
						Log.d("getCstFacility", "response 실패${response.errorBody()}")
					}
				}
				override fun onFailure(call: Call<JsonObject>, t: Throwable) {
					binding.noneImageTV.visibility = View.VISIBLE
					Log.d("getCstFacility", "response 실패2${t}")
				}
			})
		}*/
		Glide.with(requireContext()).load(0).into(binding.droneImageView)
	}
	
	override fun plus() {
		TODO("Not yet implemented")
	}
	
	override fun minus() {
		TODO("Not yet implemented")
	}
	
	override fun getCount(size: Int) {
		binding.selectedImageCountTv.text = size.toString()
	}
	
}