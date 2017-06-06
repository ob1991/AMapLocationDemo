package com.amap.location.demo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.location.demo.DB.NetUtils;
import com.amap.location.demo.DB.mysocket;
import com.amap.location.demo.DB.socket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * AMapV2地图中介绍定位几种类型
 */
	public class LocationModeSourceActivity extends Activity
		implements AMapLocationListener,
		LocationSource
//		,AdapterView.OnItemSelectedListener
	{
	private AMap aMap;
	private MapView mapView;
	private Spinner spinnerGps;
	private String[] itemLocationTypes = { "10S", "20S", "30S", "40S", "60S", "90S", "120S", "180S" };
    private final String url="http://202.118.16.50:8101/data.ashx";
	private MyLocationStyle myLocationStyle;
	private AMapLocationClient mlocationClient;
	private LocationSource.OnLocationChangedListener mListener;
	private AMapLocationClientOption mLocationOption;
    private double mylat,mylon;
	private Button bt1;
	//标识，用于判断是否只显示一次定位信息和用户重新定位
	private boolean isFirstLoc = true;
    String name,password;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what==1) {
                if(msg.obj!=null){
                    parseJSONWithJSONObject(msg.obj.toString());
                }else{

                }
            }
        }
    };
    private Handler inhandle=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what==1) {
                if(msg.obj!=null){
                    parseJSONWithJSONObject(msg.obj.toString());
                }else{
                    Toast.makeText(LocationModeSourceActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

	@Override
	protected void onStop()
	{
		super.onStop();
	}

	private static String IpAddress = "192.168.16.254";
	private static int Port = 8080;
	Socket socket = null;
	public void sendMsg() {

		try {
			// 创建socket对象，指定服务器端地址和端口号
			socket = new Socket(IpAddress, Port);
			// 获取 Client 端的输出流
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())), true);
			// 填充信息
			out.println();
			System.out.println("112312313132132");
			// 关闭

		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题栏
		setContentView(R.layout.locationmodesource_activity);
        name= this.getIntent().getStringExtra("name");
        password= this.getIntent().getStringExtra("password");
        mapView = (MapView) findViewById(R.id.map);
		bt1=(Button) findViewById(R.id.pull);
		bt1.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				new Thread() {
					@Override
					public void run() {
						sendMsg();
					}
				}.start();
			}
		});
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			aMap.setLocationSource(this);
			aMap.getUiSettings().setRotateGesturesEnabled(false);
			aMap.moveCamera(CameraUpdateFactory.zoomBy(6));
			setUpMap();
		}
//		spinnerGps = (Spinner) findViewById(R.id.spinner_gps);
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//				android.R.layout.simple_spinner_item, itemLocationTypes);
//		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		spinnerGps.setAdapter(adapter);
//
//		spinnerGps.setOnItemSelectedListener(this);
//		设置SDK 自带定位消息监听
	}
	//定位
	private void initLoc() {
		//初始化定位
		mlocationClient = new AMapLocationClient(getApplicationContext());
		//设置定位回调监听
		mlocationClient.setLocationListener(this);
		//初始化定位参数
		mLocationOption = new AMapLocationClientOption();
		//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		//设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);
		//设置是否只定位一次,默认为false
		mLocationOption.setOnceLocation(false);
		//设置是否强制刷新WIFI，默认为强制刷新
		mLocationOption.setWifiActiveScan(true);
		//设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption.setMockEnable(false);
		//设置定位间隔,单位毫秒,默认为2000ms
		mLocationOption.setInterval(2000);
		//给定位客户端对象设置定位参数
		mlocationClient.setLocationOption(mLocationOption);
		//启动定位
		mlocationClient.startLocation();
	}
	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		// 如果要设置定位的默认状态，可以在此处进行设置
		myLocationStyle = new MyLocationStyle();
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.setMapTextZIndex(2);
		//aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE));
		aMap.getUiSettings().setLogoBottomMargin(-50);
		initLoc();
        btnget();
	}

    class sendValueToServer implements Runnable
    {
        Map<String, String> map;
        public sendValueToServer(Map<String, String> map) {
            this.map=map;
        }

        @Override
        public void run() {
            String result = NetUtils.getRequest(url, map);
            Message message = Message.obtain(handler, 1, result);
            handler.sendMessage(message);
        }
    }
    private void btnget()
    {
        try {
            name = new String(name.getBytes("ISO8859-1"), "UTF-8");
            password = new String(password.getBytes("ISO8859-1"), "UTF-8");
            Map<String, String> map=new HashMap<String, String>();
            map.put("name",name);
            map.put("password", password);
            Thread a=new Thread(new LocationModeSourceActivity.sendValueToServer(map));
			a.start();
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
	private void parseJSONWithJSONObject(String JsonData) {
		try
		{
			JSONArray jsonArray = new JSONArray(JsonData);
			for (int i=0; i < jsonArray.length(); i++)    {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
                LatLng latLng=new LatLng(Double.parseDouble(jsonObject.getString("Startpointx")),Double.parseDouble(jsonObject.getString("Startpointy")));
                LatLng latLng1=new LatLng(Double.parseDouble(jsonObject.getString("Endpointx")),Double.parseDouble(jsonObject.getString("Endpointy")));
                addPolylinesWithColors(latLng,latLng1,Float.parseFloat(jsonObject.getString("Width")),Integer.parseInt(jsonObject.getString("Catlog")),Integer.parseInt(jsonObject.getString("Type")));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	@Override
	public void activate(LocationSource.OnLocationChangedListener listener) {
		mListener = listener;
	}


	@Override
	public void deactivate() {
		mListener = null;
		if (mlocationClient != null) {
			mlocationClient.stopLocation();
			mlocationClient.onDestroy();
		}
		mlocationClient = null;
	}

	private void addPolylinesWithColors(LatLng latlng1, LatLng latlng2, float width, int type, int catelog) {
		//用一个数组来存放颜色，四个点对应三段颜色
		List<Integer> colorList = new ArrayList<Integer>();
		if(type==1&&catelog==1)
		colorList.add(Color.RED);
		else if(type==1&&catelog==2)
		colorList.add(Color.YELLOW);
		else if(type==2&&catelog==1)
		colorList.add(Color.GREEN);
		else if(type==2&&catelog==2)
		colorList.add(Color.BLACK);

		PolylineOptions options = new PolylineOptions();
		options.width(width*10);//设置宽度

		//加入四个点
		options.add(latlng1,latlng2);

		//加入对应的颜色,使用colorValues 即表示使用多颜色，使用color表示使用单色线
		options.colorValues(colorList);

		aMap.addPolyline(options);
	}
//	@Override
//	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//		switch (position) {
//			case 0:
//				// 只定位，不进行其他操作
//				aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW));
//				break;
//			case 1:
//				aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE));
//				break;
//			case 2:
//				// 设置定位的类型为 跟随模式
//				aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW));
//				break;
//			case 3:
//				// 设置定位的类型为根据地图面向方向旋转
//				aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE));
//				break;
//			case 4:
//				// 定位、且将视角移动到地图中心点，定位点依照设备方向旋转，  并且会跟随设备移动。
//				aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE));
//				break;
//			case 5 :
//				// 定位、但不会移动到地图中心点，并且会跟随设备移动。
//				aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER));
//				break;
//			case 6 :
//				// 定位、但不会移动到地图中心点，地图依照设备方向旋转，并且会跟随设备移动。
//				aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER));
//				break;
//			case 7 :
//				// 定位、但不会移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。
//				aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER));
//				break;
//		}
//	}
//
//	@Override
//	public void onNothingSelected(AdapterView<?> parent) {
//
//	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		mListener = null;
		if (mlocationClient != null) {
			mlocationClient.stopLocation();
			mlocationClient.onDestroy();
		}
		mlocationClient = null;
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null) {
			if (amapLocation.getErrorCode() == 0) {
				//定位成功回调信息，设置相关消息
				 amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
				mylat = amapLocation.getLatitude();//获取纬度
				mylon = amapLocation.getLongitude();//获取经度
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date(amapLocation.getTime());
				df.format(date);//定位时间


				// 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
				if (isFirstLoc) {
					//设置缩放级别
					aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
					//将地图移动到定位点
					aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
					//点击定位按钮 能够将地图的中心移动到定位点
					mListener.onLocationChanged(amapLocation);
					//获取定位信息
					StringBuffer buffer = new StringBuffer();
					buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() + "" + amapLocation.getProvince() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
					Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
					isFirstLoc = false;
				}
			} else {

				Log.e("AmapError", "location Error, ErrCode:"
						+ amapLocation.getErrorCode() + ", errInfo:"
						+ amapLocation.getErrorInfo());
				Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
			}
		}
	}
}