/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Roads;

import dipl_project.Dipl_project;
import dipl_project.Roads.VehicleGenerating.StartCar;
import dipl_project.Roads.VehicleGenerating.StartSegment;
import dipl_project.Roads.VehicleGenerating.StartTram;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javafx.geometry.Bounds;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Shape;
import javafx.util.Pair;

/**
 *
 * @author Honza
 */
public class RoadCreator {
    private Point pOld, pNew;
    private final int DEF_LENGTH=30;
    private  double segLength=DEF_LENGTH, arrowLengthMin=120, collisDistance=30;
    private RoadSegment lastRS, newRS;
    private MyCurve actualCurve;
    private boolean newCurve, newSegment=true, zooming=false;
    private double curveLength=0, startAngle=0, startControllLenght=0;
    private int curveSegmentsSize=0, newCurveSegmentsSize=0;
    private List<StartSegment> startCarSegments=new ArrayList<>();
    private List<StartSegment> startTramSegments=new ArrayList<>();
    private List<MyCurve> curves;
    private int idMax=0;
    public RoadCreator() {
    }
    public void enableNewSegment(boolean enable)
    {
        newSegment=enable;
    }
    public void createRoad(List<Connect> connects, List<MyCurve> curves)
    {
        this.curves=curves;
        for (Connect connect : connects) {
            
            for (MyCurve startCurve : connect.getStartCurves()) { 
                startAngle=MyMath.angle(startCurve.getP0(), startCurve.getP1());
                startControllLenght=MyMath.length(connect.getLocation(), startCurve.getStartControll().getLocation());
                curveSegmentsSize=0;
                newCurveSegmentsSize=0;
                lastRS=null;
                pOld=connect.getLocation();
                actualCurve=startCurve;
                curveSegmentsSize=actualCurve.getCurveSegments().size();
                RoadSegment firtstRSShort=actualCurve.getFirstCurveSegment();
                
                callBez(startCurve.getCurve());
                if(newCurveSegmentsSize<1)
                {
                    if(firtstRSShort!=null){
                        actualCurve.getFirstCurveSegment().removeSegment();
                        actualCurve.getCurveSegments().clear();
                    }
                    newShortCurveSegment(actualCurve);
                    
                }
                
                
                List<RoadSegment>curveSegments=actualCurve.getCurveSegments();
                
                if(curveSegmentsSize>newCurveSegmentsSize && !zooming)
                {
                    
                    int size=curveSegmentsSize;
                    List<RoadSegment> segments;
                    for (int i = size-1; i > newCurveSegmentsSize-1; i--) {
                        segments=curveSegments.get(i).getRsLast();
                        for (RoadSegment segment : segments) {
                            if(segment.getRsNext().contains(curveSegments.get(i)))
                            {
                                lastRS=segment;
                                lastRS.getRsNext().clear();
                                break;
                            }
                        }
                        curveSegments.get(i).removeSegment();
                        curveSegments.remove(i);
                    }
                }  
            }
            setBlinkers(connect);
        }
        setArrows();
        for (Connect connect : connects) {
            List<MyCurve> startCurves=connect.getStartCurves();
            List<MyCurve> endCurves=connect.getEndCurves();  
            for (MyCurve startCurve : startCurves) {
                for (MyCurve endCurve : endCurves) {
                    RoadSegment rsEnd=endCurve.getLastCurveSegment();
                    RoadSegment rsStart=startCurve.getFirstCurveSegment(); 
                    RoadSegment rsNew=connect.getConnectSegment(startCurve, endCurve);
                    if(rsNew==null)
                    {
                        rsNew=new RoadSegment(new Point(rsEnd.getP3()), new Point(rsStart.getP0()));
                        rsNew.setId(idMax);
                        rsNew.setVisible(false);
                        idMax++;
                        rsNew.setSegmentLenght(MyMath.length(rsNew.getP0(), rsNew.getP3()));
                        connect.addConnectSegment(rsNew, startCurve, endCurve);
                    }
                    else
                    {
                        rsNew.setP0(new Point(rsEnd.getP3()));
                        rsNew.setP3(new Point(rsStart.getP0()));
                        rsNew.disconnectRS();
                        
                    }
                    double lenght=MyMath.length(rsNew.getP0(), rsNew.getP3());
                    rsNew.setSegmentLenght(lenght);
                    Point p1=MyMath.rotate(rsEnd.getP3(), lenght/3,  MyMath.angle(rsNew.getP0(), rsEnd.getP2()));
                    rsNew.setP1(p1);
                    
                    Point p2=MyMath.rotate(rsStart.getP0(), lenght/3, MyMath.angle(p1,rsStart.getP0()));
                    rsNew.setP2(p2);
                    connectSegments(rsEnd, rsNew);
                    connectSegments(rsNew, rsStart);
                
                }
            }
            
        }    
        List<StartSegment>newStartsCar=new ArrayList<>();
        List<StartSegment>newStartsTram=new ArrayList<>();
        for (Connect connect : connects) {
                checkSameWay(connect);
            if(connect.getEndCurves().isEmpty())
            {
                for (MyCurve startCurve : connect.getStartCurves()) {
                    RoadSegment startSegment=startCurve.getFirstCurveSegment();
                    StartSegment startCurveSegment=startCurve.getStartSegment();
                    
                    if(!startCurve.isTramCurve()){
                        
                        for (StartSegment startCarSegment : startCarSegments) {
                            if(startCarSegment.getStartRS().equals(startSegment))
                            {
                                newStartsCar.add(startCarSegment);
                            }
                        }
                        if(!newStartsCar.contains(startCurveSegment))
                        {
                            StartCar start=new StartCar(startSegment, Dipl_project.getSc().getFrequencyCarGeneration(), connect,startCurve);
                            newStartsCar.add(start);
                            startCurve.setStartStreet(start);
                        }
                        
                    }
                    else{
                        
                        for (StartSegment startTramSegment : startTramSegments) {
                            if(startTramSegment.getStartRS().equals(startSegment))
                            {
                                newStartsTram.add(startTramSegment);
                            }
                        }
                        if(!newStartsTram.contains(startCurveSegment))
                        {
                            StartTram start=new StartTram(startSegment, Dipl_project.getSc().getFrequencyTramGeneration(), connect,startCurve);
                            newStartsTram.add(start);
                            startCurve.setStartStreet(start);
                        }
                        
                    }
                }
            }
        }
        for (StartSegment startCarSegment : startCarSegments) {
            if(!startCarSegment.getStartConnect().getEndCurves().isEmpty())
                startCarSegment.getMc().setStartStreet(null);
        }
        startCarSegments.clear();
        startCarSegments.addAll(newStartsCar);
        
        for (StartSegment startTramSegment : startTramSegments) {
            if(!startTramSegment.getStartConnect().getEndCurves().isEmpty())
                startTramSegment.getMc().setStartStreet(null);
        }
        startTramSegments.clear();
        startTramSegments.addAll(newStartsTram);
    }
    private void newShortCurveSegment(MyCurve mc)
    {
        Point p0=new Point(mc.getP0());
        Point p3=new Point(mc.getP3());
        newRS=new RoadSegment(p0,p3);
        double segmentLenght=MyMath.length(p0, p3);
        newRS.setSegmentLenght(segmentLenght);
        newRS.setId(idMax);
        if(!mc.getStartConnect().getEndCurves().isEmpty())
        {
            Point p1=MyMath.rotate(p0, segmentLenght/3,  MyMath.angle(p0, mc.getStartConnect().getEndCurves().get(0).getLastCurveSegment().getP2()));
            newRS.setP1(p1);
            Point p2=MyMath.rotate(p0, segmentLenght/3, MyMath.angle(p1,p0));
            newRS.setP2(p2);
        }
        else
        {
            Point p1=MyMath.rotate(p0, segmentLenght/3,  MyMath.angle(p0, p3));
            newRS.setP1(p1);
            Point p2=MyMath.rotate(p0, segmentLenght*2/3, MyMath.angle(p0,p3));
            newRS.setP2(p2);
        }
        mc.addCurveSegments(newRS);
        curveSegmentsSize=1;
        newCurveSegmentsSize=1;
        idMax++;
    }
    public void findIntersects()
    {
        for (MyCurve curve : curves) {
            List<MyCurve> intersectedCurves=findCurveIntersect(curve);
            for (MyCurve intersectedCurve : intersectedCurves) {
                findRoadSegmentIntersect(intersectedCurve, curve);
                
            }
        }
    }
    private void checkEndBlinkers(Connect connect)
    {
         if(connect.getEndCurves().size()==1)
        {
            
            connect.getEndCurves().get(0).getFirstCurveSegment().setBlinkerLeft(false);
            connect.getEndCurves().get(0).getFirstCurveSegment().setBlinkerRight(false);
        }
        
    }
    private void setBlinkers(Connect connect)
    {
        for (MyCurve startCurve : connect.getStartCurves()) {
            RoadSegment firstCurveSegment=startCurve.getFirstCurveSegment();
            if(firstCurveSegment!=null)
            {
               firstCurveSegment.setBlinkerLeft(false);
               firstCurveSegment.setBlinkerRight(false);
            }
            RoadSegment lastCurveSegment=startCurve.getLastCurveSegment();
            if(lastCurveSegment!=null)
                lastCurveSegment.setStopBlinker(false);
        }
        if(connect.getStartCurves().size()>1)
        {
            List<BlinkerAngle> angles=new ArrayList<>();
            double angleDef=MyMath.angle(connect.getStartCurves().get(0).getP0(), connect.getStartCurves().get(0).getP1());
            for (MyCurve startCurve : connect.getStartCurves()) {
                
                
                
                double angle=MyMath.angle(startCurve.getP1(), startCurve.getP3());
                angle-=angleDef;
                angles.add(new BlinkerAngle(angle, startCurve));
            }
            angles.sort(new Comparator<BlinkerAngle>() {
                @Override
                public int compare(BlinkerAngle o1, BlinkerAngle o2) {
                    return o1.getAngle()>o2.getAngle() ? -1 : (o1.getAngle() < o2.getAngle() ? 1 :0);
                }
            });
            BlinkerAngle baLeft=angles.get(angles.size()-1);
            BlinkerAngle baRight=angles.get(0);
            if(angles.size()>2)
            {
                baLeft.setRun(true);
                baRight.setRun(true);
                
            }
            else
            {
                boolean runLeft=(Math.abs(baLeft.getAngle())-Math.abs(baRight.getAngle()))>0;
                baLeft.setRun(runLeft);
                baRight.setRun(!runLeft);
            }
            
            baLeft.getMc().getFirstCurveSegment().setBlinkerLeft(baLeft.isRun());
            baRight.getMc().getFirstCurveSegment().setBlinkerRight(baRight.isRun());
             baLeft.getMc().getLastCurveSegment().setStopBlinker(true);
             baRight.getMc().getLastCurveSegment().setStopBlinker(true);
        }
        else if(connect.getStartCurves().size()>0 && connect.getEndCurves().size()>0)
        {
            MyCurve mcBlinker=connect.getStartCurves().get(0);
            double angle1=MyMath.angle(mcBlinker.getP0(), mcBlinker.getP1());
            double angle2=MyMath.angle(mcBlinker.getP1(), mcBlinker.getP3());
            if(angle1>Math.PI || angle2>Math.PI)
            {
                angle1-=Math.PI;
                angle2-=Math.PI;
            }
            angle2-=angle1;
            angle1=0;
            boolean right=((angle2>0 || angle2<-Math.PI) && angle2<Math.PI);
            if(Math.abs(angle2)>Math.PI/6)
            {
                if(right)
                {
                    mcBlinker.getFirstCurveSegment().setBlinkerLeft(false);
                    mcBlinker.getFirstCurveSegment().setBlinkerRight(true);
                }
                else
                {
                    mcBlinker.getFirstCurveSegment().setBlinkerLeft(true);
                    mcBlinker.getFirstCurveSegment().setBlinkerRight(false);
                }
                
                mcBlinker.getLastCurveSegment().setStopBlinker(true);
            }
            
                
        }
    }
    private List<MyCurve> findCurveIntersect(MyCurve curve)
    {
        List<MyCurve> intersectedCurves=new ArrayList<MyCurve>();
        for (MyCurve c : curves) {
            Bounds interBounds=Shape.intersect(curve.getCurve(), c.getCurve()).getBoundsInLocal();
            if(interBounds.getWidth()>0 || interBounds.getHeight()>0)
            {
                intersectedCurves.add(c);
            }
        }
        return intersectedCurves;
    }
    private void findRoadSegmentIntersect(MyCurve curve1, MyCurve curve2)
    {
        boolean newIntersect=true;
        for (RoadSegment curveSegment1 : curve1.getCurveSegments()) {
            for (RoadSegment curveSegment2 : curve2.getCurveSegments()) {
                boolean intersect=false;
                if(!curveSegment1.equals(curveSegment2))
                {
                    if(!curveSegment1.getRsLast().contains(curveSegment2) && !curveSegment1.getRsNext().contains(curveSegment2))
                    {
                        Bounds interBounds=Shape.intersect(curveSegment1.getShape(), curveSegment2.getShape()).getBoundsInLocal();
                        if(interBounds.getWidth()>0 || interBounds.getHeight()>0)
                        {
                            intersect=true;
                            for (RoadSegment rsLast : curveSegment1.getRsLast()) {
                                if(curveSegment2.getRsLast().contains(rsLast))
                                    intersect=false;
                            }
                        }
                    }
                }
                
                if(intersect)
                {
                    
                    boolean add=true;
                    for (RoadSegment roadSegment : curveSegment1.getRsLast()) {
                        if(!roadSegment.getIntersectedRoadSegments().isEmpty())
                        {
                            add=false;
                        }  
                    }
                    if(add && newIntersect)
                    {
                        curveSegment2.addIntersectedRS(curveSegment2);
                        newIntersect=false;
                    }  
                } 
                else
                {
                    curveSegment1.removeIntersectedRS(curveSegment2);
                }
                    
            }
        }
    }

    public List<StartSegment> getStartCarSegments() {
        return startCarSegments;
    }

    public void setStartCarSegments(List<StartSegment> startCarSegments) {
        this.startCarSegments = startCarSegments;
    }

    public List<StartSegment> getStartTramSegments() {
        return startTramSegments;
    }

    public void setStartTramSegments(List<StartSegment> startTramSegments) {
        this.startTramSegments = startTramSegments;
    }

    
    
    

    private void connectSegments(RoadSegment oldRS, RoadSegment newRS)
    {
        
        if(oldRS!=null && newRS!=null){
            if(!oldRS.getRsNext().contains(newRS))
                oldRS.addNextRs(newRS);
            if(!newRS.getRsLast().contains(oldRS))
                newRS.addLastRs(oldRS);
            
            
        }
    }
    
    private void checkSameWay(Connect connect)
    { 

        for(Map.Entry<Pair<MyCurve, MyCurve>, RoadSegment> rs1 : connect.getConnectSegmentsMap().entrySet()) {
            for(Map.Entry<Pair<MyCurve, MyCurve>, RoadSegment> rs2 : connect.getConnectSegmentsMap().entrySet()) {
                if(!rs1.equals(rs2))
                {

                    isSameWayLast(rs1.getValue(), rs2.getValue());
                    for (RoadSegment rsNext1 : rs1.getValue().getRsNext()) {
                        for (RoadSegment rsNext2 : rs2.getValue().getRsNext()) {
                            if(!rsNext1.equals(rsNext2))
                            {
                                isSameWayNext(rsNext1, rsNext2);
                            }
                        }
                    }
                }
            }
        }
        
       
    }
    private void isSameWayLast(RoadSegment rs1, RoadSegment rs2)
    {
        double distanceLast=Math.min(MyMath.distanceFromLine(rs1.getP0(),rs1.getP3(), rs2.getP0()), MyMath.distanceFromLine(rs1.getP0(),rs1.getP3(), rs2.getP3()));
        boolean sameWayLast= distanceLast<collisDistance;
        joinSW(rs1, rs2, sameWayLast);
        if(sameWayLast)
        {
            for (RoadSegment rsLast1 : rs1.getRsLast()) {
                for (RoadSegment rsLast2 : rs2.getRsLast()) {
                    if(!rsLast1.equals(rsLast2))
                    {
                        isSameWayLast(rsLast1, rsLast2);
                    }
                }
            }
        }
    }
    private void isSameWayNext(RoadSegment rs1, RoadSegment rs2)
    {
        
        double distanceNext=Math.min(MyMath.distanceFromLine(rs1.getP0(),rs1.getP3(), rs2.getP0()), MyMath.distanceFromLine(rs1.getP0(),rs1.getP3(), rs2.getP3()));
        boolean sameWayNext= distanceNext<collisDistance;
        joinSW(rs1, rs2, sameWayNext);
        if(sameWayNext)
        {
            for (RoadSegment rsNext1 : rs1.getRsNext()) {
                for (RoadSegment rsNext2 : rs2.getRsNext()) {
                    if(!rsNext1.equals(rsNext2))
                    {
                        isSameWayNext(rsNext1, rsNext2);
                    }
                }
            }
        }
    }
    private void joinSW(RoadSegment rs1, RoadSegment rs2, boolean connect)
    {
        if(connect){
            if(!rs1.getRsSameWay().contains(rs2) && !rs2.getRsSameWay().contains(rs1))
            {
                rs1.addRsSameWay(rs2);
                rs2.addRsSameWay(rs1);
                rs1.setSameWayRS(true);
                rs2.setSameWayRS(true);
            }
                
        }
        else
        {
            if(rs1.getRsSameWay().contains(rs2))
                rs1.removeRsSameWay(rs2);
            if(rs2.getRsSameWay().contains(rs1))
                rs2.removeRsSameWay(rs1);
            
            rs1.setSameWayRS(false);
            rs2.setSameWayRS(false);
        } 
    }
    private void newSegment(int x, int y)
    {
        double length=MyMath.length(pOld.getX(), pOld.getY(), x, y);
        if(length>=segLength)
        {
            pNew=new Point(x,y);
            
            if(newCurveSegmentsSize+1>curveSegmentsSize)
            {
                if(newSegment)
                {
                    newRS=new RoadSegment(new Point(pOld), pNew);
                    newRS.setSegmentLenght(length);
                    newRS.setId(idMax);
                    actualCurve.addCurveSegments(newRS);
                    curveSegmentsSize++;
                    idMax++;
                    if(lastRS!=null)
                        connectSegments(lastRS, newRS);
                }
                
            }
            else
            {
                newRS=actualCurve.getCurveSegments().get(newCurveSegmentsSize);
                newRS.getRsSameWay().clear();
                newRS.setSameWayRS(false);
                newRS.setDefRoadSegment();
                newRS.setP0(new Point(pOld));
                newRS.setP3(pNew);
                newRS.moveSegment(pNew);
            }
                
            newCurveSegmentsSize++;
            
            if(lastRS==null)
            {
                Point p1=MyMath.rotate(pOld, length/2, startAngle+Math.PI);
                newRS.setP1(p1);
                //Point p2=MyMath.rotate(pNew, length/3, startAngle);
                newRS.setP2(new Point(p1));
            }
            else
            {
                //double angle=MyMath.angle(lastRS.getP1(),pOld);
                double angle=MyMath.angle(pOld, pNew);
                Point p2Old=MyMath.rotate(pOld, segLength/3, angle);
                lastRS.setP2(p2Old);
                Point p1=MyMath.rotate(pOld, segLength/3,  MyMath.angle(pOld, lastRS.getP2()));
                newRS.setP1(p1);
                Point p2=MyMath.rotate(pNew, segLength/3, MyMath.angle(p1,pNew));
                newRS.setP2(p2);
            }
            lastRS=newRS;
            pOld=pNew;
            
            
        }
        
    }

    public void setId(int idMax) {
        this.idMax = idMax;
    }
    
    private void callBez(CubicCurve curve)
    {
        bezier(new Point((int)curve.getStartX(), (int)curve.getStartY()), 
            new Point((int)curve.getControlX1(), (int)curve.getControlY1()),
            new Point((int)curve.getControlX2(), (int)curve.getControlY2()),
            new Point((int)curve.getEndX(), (int)curve.getEndY()));
    }
    private void bezier(Point p0, Point p1, Point p2, Point p3)
    {
        int x0=(int)p0.getX();
        int y0=(int)p0.getY();
        int x1=3*((int)p1.getX()-x0);
        int y1=3*((int)p1.getY()-y0);
        int x2=3*(x0-2*(int)p1.getX()+(int)p2.getX());
        int y2=3*(y0-2*(int)p1.getY()+(int)p2.getY());
        int x3=3*((int)p1.getX()-(int)p2.getX())+(int)p3.getX()-x0;
        int y3=3*((int)p1.getY()-(int)p2.getY())+(int)p3.getY()-y0;
        int xfirst=(int)p0.getX();
        int yfirst=(int)p0.getY();
        
        curveLength=0;
        for(float t = 0; t <= 1; t +=0.01) {
            float t2=t*t;
            float t3=t2*t;
            int x = (int)(x0+(t*x1)+(t2*x2)+(t3*x3));
            int y = (int)(y0+(t*y1)+(t2*y2)+(t3*y3));
            line(xfirst, yfirst,x,y);
            xfirst=x;
            yfirst=y;   
        }
        actualCurve.setCurveLenght(curveLength);
        
    }
    public void setArrows()
    {
        for (MyCurve curve : Dipl_project.getUI().getCurves()) {
            int countOfArrows=(int)(curve.getCurveLenght()/arrowLengthMin);
            List<Arrow> arrows = curve.getArows();
            List<RoadSegment> segments = curve.getCurveSegments();
            if(countOfArrows>0)
            {
                for (int i = 1; i <= countOfArrows; i++) {
                    double time=(1/((double)countOfArrows+1))*i;

                    RoadSegment seg = segments.get((int)(segments.size()*time));
                    Point p=MyMath.getLinePointAtT(seg.getP0(),seg.getP3(), 0.4);
                    Point p1=MyMath.getLinePointAtT(seg.getP0(),seg.getP3(), 0.6);
                    double angle=MyMath.angle(p1, p);
                    if(arrows.size()<countOfArrows)
                    {
                            curve.addArrow(new Arrow(angle, p.getX(), p.getY()));
                    }
                    else
                    {
                        arrows.get(i-1).moveArrow(p.getX(), p.getY(), angle);
                    }

                }
            }
            if(arrows.size()>countOfArrows)
            {
                for (int i = arrows.size(); i > countOfArrows; i--) {
                    curve.removeArrowAt(i-1);
                }
            }
        }
        
    }
    public void line(int xA, int yA, int xB, int yB)
    {       
        int x1=xA;
        int y1=yA;
        int x2=xB;
        int y2=yB;
        int dx = Math.abs(x2-x1);
        int dy = Math.abs(y2-y1); 
        int dxy=dx-dy;    
        if(dx!=0 || dy!=0) 
        {
            int px=-1;
            int py=-1;
            if(x1<x2)px=1;
            if(y1<y2)py=1;
            newSegment(x1, y1);
            while ((x1 != x2) || (y1 != y2))
            {            
                int p = 2 * dxy;
                if (p > -dy) {
                    dxy -= dy;
                    x1 +=px;
                }
                if (p < dx) {
                    dxy += dx;
                    y1 +=py;
                }
                curveLength++;
                newSegment(x1, y1);
            }
        }   
    }

    public double getSegLenght() {
        return segLength;
    }

    public void setSegLenght(double segLength) {
        this.segLength = segLength;
        collisDistance=segLength;
    }

    public double getArrowLengthMin() {
        return arrowLengthMin;
    }

    public void setArrowLengthMin(double arrowLengthMin) {
        this.arrowLengthMin = arrowLengthMin;
    }

    public double getCollisDistance() {
        return collisDistance;
    }

    public void setCollisDistance(double collisDistance) {
        this.collisDistance = collisDistance;
    }

    public boolean isZooming() {
        return zooming;
    }

    public void setZooming(boolean zooming) {
        this.zooming = zooming;
    }

    public void setDefSegLenght() {
        segLength=DEF_LENGTH;
    }
    
}
