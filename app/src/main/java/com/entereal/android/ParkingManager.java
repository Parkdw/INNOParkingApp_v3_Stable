package com.entereal.android;

import android.util.Log;

import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-01-03.
 */
public class ParkingManager {

    public  class ParkingList {
        public ArrayList<ParkingVO> list = new ArrayList<ParkingVO>(); // 전체 리스트

        public ParkingVO Add(JSONObject obj) {
            String id = (String)obj.get("id");
            ParkingVO vo;
            if(IsExist(id)) {
                vo = Get(id);
                if(vo != null) {
                    vo.id = (String) obj.get("id");
                    vo.address = (String) obj.get("address");
                    vo.lat = Double.valueOf(obj.get("lat").toString());
                    vo.lng = Double.valueOf(obj.get("lng").toString());
                    vo.name = (String) obj.get("name");
                    vo.desc = (String) obj.get("desc");
                    vo.distance = Double.valueOf(obj.get("distance").toString());
                    if(vo.space != Integer.valueOf(obj.get("space").toString())) {
                        vo.changed = true;
                    }
                    if(vo.parked != Integer.valueOf(obj.get("parked").toString())) {
                        vo.changed = true;
                    }
                    vo.space = Integer.valueOf(obj.get("space").toString());
                    vo.parked = Integer.valueOf(obj.get("parked").toString());
                    Log.i("NMapViewer", "manager update " + vo.id + " " + vo.name + " " + vo.parked + " " + vo.space);
                }
            }
            else {
                vo = new ParkingVO();
                vo.id = (String) obj.get("id");
                vo.id2 = obj.get("lng").toString() + "," + obj.get("lat").toString();
                vo.address = (String) obj.get("address");
                vo.lat = Double.valueOf(obj.get("lat").toString());
                vo.lng = Double.valueOf(obj.get("lng").toString());
                vo.name = (String) obj.get("name");
                vo.desc = (String) obj.get("desc");
                vo.distance = Double.valueOf(obj.get("distance").toString());
                vo.space = Integer.valueOf(obj.get("space").toString());
                vo.parked = Integer.valueOf(obj.get("parked").toString());
                vo.changed = false;
                list.add(vo);
                Log.i("NMapViewer", "manager add " + vo.id + " " + vo.name + " " + vo.parked + " " + vo.space);
            }
            return vo;
        }

        public boolean IsExist(String id) {
            for (int i = 0; i < list.size(); i++) {
                ParkingVO vo = list.get(i);
                if(vo.id.equals(id)) {
                    return true;
                }
            }
            return false;
        }

        public ParkingVO Get(String id) {
            for (int i = 0; i < list.size(); i++) {
                ParkingVO vo = list.get(i);
                if(vo.id.equals(id)) {
                    return vo;
                }
            }
            return null;
        }

        public int GetIndex(String id) {
            for (int i = 0; i < list.size(); i++) {
                ParkingVO vo = list.get(i);
                if(vo.id.equals(id)) {
                    return i;
                }
            }
            return -1;
        }

        public  ParkingVO Get(Double lng, Double lat) {
            for (int i = 0; i < list.size(); i++) {
                ParkingVO vo = list.get(i);
                if(vo.lng == lng && vo.lat == lat) {
                    return vo;
                }
            }
            return null;
        }

        public boolean isChange() {
            for (int i = 0; i < list.size(); i++) {
                ParkingVO vo = list.get(i);
                if(vo.changed) {
                    return true;
                }
            }
            return false;
        }

        public void clearChange() {
            for (int i = 0; i < list.size(); i++) {
                ParkingVO vo = list.get(i);
                vo.changed = false;
            }
        }

        public  void Clear() {
            list.clear();
        }
    }

    public class ParkingVO {
        public String id;            // 37.9033163,127.19828 위도, 경도
        public String id2;           // 127.19828, 37.9033163 경도, 위도 네이버 POI아이템과 비교하기 위해 사용
        public String address;      // 주차장 주소
        public double lat;          // 위도
        public double lng;          // 경도
        public String name;         // 주차장 이름
        public String desc;         // 주차장 설명
        public double distance;     // 거리(인수에 lat, lng 값이 없을 경우에는 0)
        public int space;           // 주차면 수
        public int parked;          // 현재 주차된 수
        public boolean changed;

        public boolean isEmpty() {
            return space > parked;
        }

    }

    private static ParkingManager _instance;
    public ParkingList all = new ParkingList();
    public ParkingList search = new ParkingList();

    public static ParkingManager instance() {
        if(_instance == null) {
            _instance = new ParkingManager();
        }
        return _instance;
    }

}
