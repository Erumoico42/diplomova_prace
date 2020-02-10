/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dipl_project.Roads;

import dipl_project.Dipl_project;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Shape;

/**
 *
 * @author Honza
 */
public class RoadCreator {
    private Point pOld, pNew;
    private  double segLength=30, arrowLengthMin=120, collisDistance=30;
    private RoadSegment lastRS, newRS;
    private MyCurve actualCurve;
    private boolean newCurve;
    private double curveLength=0;
    private int curveSegmentsSize=0;
    private List<RoadSegment> startTramSegments=new ArrayList<>();
    private List<RoadSegment> startCarSegments=new ArrayList<>();
    private List<RoadSegment> curveSegments;
    private List<MyCurve> curves;
    private int id=0, idMax=0;
    public RoadCreator() {
    }
    public void createRoad(List<Connect> connects, List<MyCurve> curves)
    {
        this.curves=curves;
        for (Connect connect : connects) {
            
            for (MyCurve startCurve : connect.getStartCurves()) { 
                actualCurve=startCurve;
                boolean endExist=false;
                for (MyCurve endCurve : connect.getEndCurves()) {
                    RoadSegment rsEnd=endCurve.getLastCurveSegment();
                    if(rsEnd!=null)
                    {
                        pOld=rsEnd.getP3();
                        endExist=true;
                        break;
                    }
                }  
                curveSegmentsSize=0;
                curveSegments=startCurve.getCurveSegments();
                if(!endExist)
                    pOld=connect.getLocation();
                newCurve=true;
                callBez(startCurve.getCurve());
                
                if(curveSegmentsSize<curveSegments.size())
                {
                    int size=curveSegments.size();
                    List<RoadSegment> segments;
                    for (int i = size-1; i > curveSegmentsSize-1; i--) {
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
                lastRS=null;
                
                    
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
                    double angle; 
                    if(rsStart!=null)
                    {
                        if(rsStart.getRsNext()!=null && !rsStart.getRsNext().isEmpty())
                        {
                            RoadSegment rsStartNext=rsStart.getRsNext().get(0);
                            angle=MyMath.angle(rsStartNext.getP0(), rsStartNext.getP1());
                            Point p21=MyMath.rotate(rsStartNext.getP0(), segLength/2, angle);
                            rsStart.setP2(p21); 
                        }

                        angle=MyMath.angle(rsStart.getP0(), rsStart.getP1());
                        Point p21=MyMath.rotate(rsStart.getP0(), segLength/2, angle);
                        rsEnd.setP2(p21); 
                        connectSegments(rsEnd,rsStart);
                    }
                      
                }
                
            }
            
        }     
        startCarSegments.clear();
        startTramSegments.clear();
        for (Connect connect : connects) {
            if(connect.getEndCurves().isEmpty())
            {
                for (MyCurve startCurve : connect.getStartCurves()) {
                    if(!startCurve.isTramCurve())
                        startCarSegments.add(startCurve.getFirstCurveSegment());
                    else
                        startTramSegments.add(startCurve.getFirstCurveSegment());
                }
            }
            checkEndBlinkers(connect);
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
        else if(connect.getStartCurves().size()==1 && connect.getEndCurves().size()>0)
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
            if(Math.abs(angle2)>Math.PI/4)
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
    public void findIntersects()
    {
        for (MyCurve curve : curves) {
            List<MyCurve> intersectedCurves=findCurveIntersect(curve);
            for (MyCurve intersectedCurve : intersectedCurves) {
                findRoadSegmentIntersect(intersectedCurve, curve);
                
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
                        curveSegment1.addIntersectedRS(curveSegment2);
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
    
    public List<RoadSegment> getStartCarSegments() {
        return startCarSegments;
    }

    public void setStartCarSegments(List<RoadSegment> startCarSegments) {
        this.startCarSegments = startCarSegments;
    }

    public List<RoadSegment> getStartTramSegments() {
        return startTramSegments;
    }

    public void setStartTramSegments(List<RoadSegment> startTramSegments) {
        this.startTramSegments = startTramSegments;
    }
    

    private void connectSegments(RoadSegment oldRS, RoadSegment newRS)
    {
        
        if(oldRS!=null && newRS!=null){
            //rozdeleni
            
                swSplit(oldRS, newRS);
                
            
            //spojeni
                swConnect(oldRS, newRS);
            
            
            if(!oldRS.getRsNext().contains(newRS))
                oldRS.addNextRs(newRS);
            if(!newRS.getRsLast().contains(oldRS))
                newRS.addLastRs(oldRS);
            
            
        }
    }

    private boolean checkSameWay(Point prs1, Point prs2)
    { 
        double distance=MyMath.length(prs1, prs2);
        boolean sameWay= distance<collisDistance;
        return sameWay;
    }
    public void setCurves(List<MyCurve> curves)
    {
        this.curves=curves;
    }
    private void swConnect(RoadSegment rs1, RoadSegment rs2)
    {
        //set distance between end points
        for (RoadSegment rs : rs2.getRsLast()) {
            if(!rs.equals(rs1))
            {
                joinSWConnect(rs, rs1);
            }    
        }
    }
    private void joinSWConnect(RoadSegment rs1, RoadSegment rs2)
    {

        boolean connect=checkSameWay(rs1.getP3(), rs2.getP3());
        double errorAngle=Math.toDegrees(MyMath.angle(rs1.getP0(), rs1.getP3())-MyMath.angle(rs1.getP3(), rs2.getP3()));
        double errorDistRs1=0;
        double errorDistRs2=0;
        double errorDistance=MyMath.length(rs1.getP3(), rs2.getP3());
        if(errorAngle>-60 && errorAngle<60)
        {
            errorDistRs1=-errorDistance/segLength;
            errorDistRs2=errorDistance/segLength;
            
        }else if(errorAngle>120 && errorAngle<240)
        {
            errorDistRs1=errorDistance/segLength;
            errorDistRs2=-segLength/errorDistance/segLength;
        }
        rs1.setErrorDistance(errorDistRs1);
        rs2.setErrorDistance(errorDistRs2);
        boolean joined=joinedSW(rs1, rs2);
        joinSW(rs1, rs2, connect);

        if(!(!joined && !connect))
        {
            for (RoadSegment rs1N : rs1.getRsLast()) {
                for (RoadSegment rs2N : rs2.getRsLast()) {
                    joinSWConnect(rs1N, rs2N);
                }
            } 
        }
        
    }
    private void swSplit(RoadSegment rs1, RoadSegment rs2)
    {
        for (RoadSegment rs : rs1.getRsNext()) {
            if(!rs.equals(rs2))
            {
                joinSWSplit(rs, rs2);
            }    
        }
    }
    private void joinSWSplit(RoadSegment rs1, RoadSegment rs2)
    {
        boolean connect=checkSameWay(rs1.getP0(), rs2.getP0());
        boolean joined=joinedSW(rs1, rs2);
        joinSW(rs1, rs2, connect);

        if(!(!joined && !connect))
        {
            for (RoadSegment rs1N : rs1.getRsNext()) {
                for (RoadSegment rs2N : rs2.getRsNext()) {
                    joinSWSplit(rs1N, rs2N);
                }
            } 
        }
        
    }
    private boolean joinedSW(RoadSegment rs1, RoadSegment rs2)
    {
        return (rs1.getRsSameWay().contains(rs2) && rs2.getRsSameWay().contains(rs1));
    }
    private void joinSW(RoadSegment rs1, RoadSegment rs2, boolean connect)
    {
        if(connect){
            if(!rs1.getRsSameWay().contains(rs2) && !rs2.getRsSameWay().contains(rs1))
            {
                rs1.addRsSameWay(rs2);
                rs2.addRsSameWay(rs1);
            }
                
        }
        else
        {
            if(rs1.getRsSameWay().contains(rs2))
                rs1.removeRsSameWay(rs2);
            if(rs2.getRsSameWay().contains(rs1))
                rs2.removeRsSameWay(rs1);
                
        } 
    }
    private void newSegment(int x, int y)
    {
        if(MyMath.length(pOld.getX(), pOld.getY(), x, y)>segLength)
        {
            
            double angle;
            pNew=new Point(x,y);
            boolean newSegment=true;
            curveSegmentsSize++;
            
            newCurve=false;
            if(curveSegments.size()>=curveSegmentsSize){
                newSegment=false;
                newRS=curveSegments.get(curveSegmentsSize-1);
                newRS.setP0(new Point(pOld));
                newRS.setP3(pNew);
                newRS.moveSegment(pNew);
                newRS.clearRsSameWay();
                newRS.setErrorDistance(0);
                if(newRS.getId()>idMax)
                    idMax=newRS.getId()+1;
            }
            else{
                if(lastRS!=null)
                    lastRS.removeNext();
                newRS=new RoadSegment(new Point(pOld), pNew);
                actualCurve.addCurveSegments(newRS);
                newRS.setMainCurve(actualCurve);
                id++;
                newRS.setId(id);
                
            }
            if(lastRS==null)
            {
                angle=MyMath.angle(pNew, pOld);
                Point p12=MyMath.rotate(pOld, segLength/2, angle);
                //Point p2=MyMath.rotate(pOld, 15, angle);
                newRS.setP1(p12);
                newRS.setP2(p12);
            }
            else
            {
                angle=MyMath.angle(pOld, lastRS.getP2());
                Point p12=MyMath.rotate(pOld, segLength/3, angle);
                newRS.setP1(p12);
                
                angle=MyMath.angle(p12, pNew);
                Point p21=MyMath.rotate(pNew, segLength/3, angle);
                newRS.setP2(p21);  
                if(newSegment)
                    connectSegments(lastRS, newRS);
            }
            for (RoadSegment rs2 : newRS.getRsNext()) {
                swSplit(newRS, rs2);
            }

            for (RoadSegment rs2 : newRS.getRsNext()) {
                swConnect(newRS, rs2);
            }
            idMax++;
            lastRS=newRS;
            pOld=pNew;
        }
        
    }

    public void setId(int id) {
        this.id = id;
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
    
}
