package com.amap.location.demo;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import java.lang.reflect.Type;

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
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.location.demo.DB.NetUtils;
import com.amap.location.demo.DB.mark;
import com.amap.location.demo.DB.socket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
		,AdapterView.OnItemSelectedListener
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
		private Button bt2;
		private int time=30;
        //标识，用于判断是否只显示一次定位信息和用户重新定位
        private boolean isFirstLoc = true;
        String name,password;
//        ACache mCache = ACache.get(this);
		private Handler handler=new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				if (msg.obj != null) {
					parseJSONWithJSONObject(msg.obj.toString());
				} else {
				}
			}
		}
	};

		private Handler savehandler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					if (msg.obj.toString().length()<=0) {
						Toast.makeText(LocationModeSourceActivity.this, "未连接网络", Toast.LENGTH_SHORT).show();
					} else {
					}
				}
			}
		};
    private Handler inhandle=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if(msg.obj!=null){
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String date=df.format(new Date(System.currentTimeMillis()));//定位时间
				LatLng latlng =new LatLng(mylat,mylon);
				mark a=new mark(date,latlng,Double.valueOf(msg.obj.toString()));
				save(a.toString());
                Toast.makeText(LocationModeSourceActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(LocationModeSourceActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
            }
        }
    };

	public void save(String content) {
		try {
			FileOutputStream outStream = this.openFileOutput("b.txt", Context.MODE_APPEND);
			String load=load();
			if(load.length()==0)
			{
				outStream.write("[".getBytes());
				outStream.write(content.getBytes());
			}
			else if(load=="[")
			outStream.write(content.getBytes());
			else
			{
				outStream.write(",".getBytes());
				outStream.write(content.getBytes());
			}
			outStream.close();
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			return;
		}
	}
	public void complicate(){
		try {
			FileOutputStream outStream = this.openFileOutput("b.txt", Context.MODE_APPEND);
			String load=load();
			if(load.length()!=0&&load.indexOf(']')==-1)
			{
				outStream.write("]".getBytes());
			}
			outStream.close();
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			return;
		}
	}
	public void clear() {
		try {
			FileOutputStream outStream = this.openFileOutput("b.txt", Context.MODE_PRIVATE);
			outStream.write("".getBytes());
			outStream.close();
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			return;
		}
	}
	public String load() {
		try {
			FileInputStream inStream = this.openFileInput("b.txt");
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				stream.write(buffer, 0, length);
			}
			stream.close();
			inStream.close();
			return stream.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	@Override
	protected void onStop()
	{
		super.onStop();
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
		bt2=(Button) findViewById(R.id.commit);
		bt2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				complicate();
				String kkk = load();
				if(kkk.length()!=0) {
					Gson gson = new Gson();
					Type type = new TypeToken<List<mark>>() {
					}.getType();
					List<mark> studentList = gson.fromJson(kkk, type);
					for (mark ma : studentList) {
						savetodb(ma.date, ma.mylatlng.latitude, ma.mylatlng.longitude, ma.temperature);
					}
					clear();
				}
				else
					Toast.makeText(LocationModeSourceActivity.this,"本地缓存为空", Toast.LENGTH_SHORT).show();
			}
		});
		bt1.setText("开始");
		bt1.setOnClickListener(new View.OnClickListener(){
			socket mysocket = new socket(inhandle, "192.168.16.254", 8080, time*1000);
			@Override
			public void onClick(View view) {
				if(bt1.getText().toString()=="开始") {
					inhandle.removeCallbacks(mysocket);
					mysocket=null;
					mysocket = new socket(inhandle, "192.168.16.254", 8080, time*1000);
					mysocket.start();
					bt1.setText("结束");
				}
				else{
					mysocket.close();
//					inhandle.removeCallbacks(mysocket);
					mysocket=null;
					bt1.setText("开始");
				}
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
		spinnerGps = (Spinner) findViewById(R.id.spinner_gps);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, itemLocationTypes);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerGps.setAdapter(adapter);
		spinnerGps.setOnItemSelectedListener(this);
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
    //保存数据
    private  String saveurl="http://202.118.16.50:8101/save.ashx";
	class sendValueToSave implements Runnable {
		Map<String, String> map;
		public sendValueToSave(Map<String, String> map) {
			this.map = map;
		}
		@Override
		public void run() {
			String result = NetUtils.getRequest(saveurl, map);
			Message message = Message.obtain(savehandler, 1, result);
			savehandler.sendMessage(message);
		}
	}
	private void savetodb(String time,double lat,double lon,double temp)
	{
		try {
			time = new String(time.getBytes("ISO8859-1"), "UTF-8");
			Map<String, String> map=new HashMap<String, String>();
			map.put("date",time);
			map.put("lat", Double.toString(lat));
			map.put("lon", Double.toString(lon));
			map.put("temperature",Double.toString(temp));
			Thread a=new Thread(new LocationModeSourceActivity.sendValueToSave(map));
			a.start();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
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
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
			case 0:
				time=10;
				break;
			case 1:
				time=20;
				break;
			case 2:
				time=30;
				break;
			case 3:
				time=40;
				break;
			case 4:
				time=60;
				break;
			case 5 :
				time=90;
				break;
			case 6 :
				time=120;
				break;
			case 7 :
				time=180;
				break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		time=30*100;
	}

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
				String aaa=df.format(date);//定位时间
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