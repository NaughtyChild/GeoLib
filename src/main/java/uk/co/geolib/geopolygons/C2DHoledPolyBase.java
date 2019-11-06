
package uk.co.geolib.geopolygons;

import uk.co.geolib.geolib.*;

import java.util.ArrayList;

public class C2DHoledPolyBase {
    /**
     * Constructor.
     */
    public C2DHoledPolyBase() {
    }

    /**
     * Constructor.
     *
     * @param Other Other polygon to set this to.
     */
    public C2DHoledPolyBase(C2DHoledPolyBase Other) {
        Rim = new C2DPolyBase(Other.Rim);

        for (int i = 0; i < Other.getHoleCount(); i++) {
            Holes.add(new C2DPolyBase(Other.GetHole(i)));
        }
    }


    /**
     * Constructor.
     *
     * @param Other Other polygon to set this to.
     */
    public C2DHoledPolyBase(C2DPolyBase Other) {
        Rim = new C2DPolyBase(Other);
    }


    /**
     * Assignment.
     *
     * @param Other Other polygon to set this to.
     */
    public void Set(C2DHoledPolyBase Other) {
        Rim.Set(Other.Rim);
        Holes.clear();
        for (int i = 0; i < Other.getHoleCount(); i++) {
            Holes.add(new C2DPolyBase(Other.GetHole(i)));
        }
    }

    /**
     * Return the number of lines.
     */
    public int GetLineCount() {
        int nResult = 0;

        nResult += Rim.Lines.size();

        for (int i = 0; i < Holes.size(); i++) {
            nResult += Holes.get(i).Lines.size();
        }

        return nResult;
    }

    /**
     * Clears the shape.
     */
    public void Clear() {
        Rim.Clear();

        Holes.clear();
    }

    /**
     * Validity check. True if the holes are contained and non-intersecting.
     */
    public boolean IsValid() {
        for (int i = 0; i < Holes.size(); i++) {
            if (!Rim.Contains(Holes.get(i)))
                return false;
        }

        int h = 0;
        while (h < Holes.size()) {
            int r = h + 1;
            while (r < Holes.size()) {
                if (Holes.get(h).Overlaps(Holes.get(r)))
                    return false;
                r++;
            }
            h++;
        }
        return true;
    }

    /**
     * Rotates to the right by the angle around the origin.
     *
     * @param dAng   Angle in radians to rotate by.
     * @param Origin The origin.
     */
    public void RotateToRight(double dAng, C2DPoint Origin) {
        Rim.RotateToRight(dAng, Origin);

        for (int i = 0; i < Holes.size(); i++) {
            Holes.get(i).RotateToRight(dAng, Origin);
        }
    }

    /**
     * Moves the polygon.
     *
     * @param Vector Vector to move by.
     */
    public void Move(C2DVector Vector) {
        Rim.Move(Vector);

        for (int i = 0; i < Holes.size(); i++) {
            Holes.get(i).Move(Vector);
        }
    }

    /**
     * Grows around the origin.
     *
     * @param dFactor Factor to grow by.
     * @param Origin  Origin to grow this around.
     */
    public void Grow(double dFactor, C2DPoint Origin) {
        Rim.Grow(dFactor, Origin);

        for (int i = 0; i < Holes.size(); i++) {
            Holes.get(i).Grow(dFactor, Origin);
        }

    }

    /**
     * Point reflection.
     *
     * @param Point Point through which to reflect this.
     */
    public void Reflect(C2DPoint Point) {
        Rim.Reflect(Point);

        for (int i = 0; i < Holes.size(); i++) {
            Holes.get(i).Reflect(Point);
        }
    }

    /**
     * Reflects throught the line provided.
     *
     * @param Line Line through which to reflect this.
     */
    public void Reflect(C2DLine Line) {
        Rim.Reflect(Line);

        for (int i = 0; i < Holes.size(); i++) {
            Holes.get(i).Reflect(Line);
        }
    }

    /**
     * Distance from the point.
     *
     * @param TestPoint Point to find the distance to.
     */
    public double Distance(C2DPoint TestPoint) {
        double dResult = Rim.Distance(TestPoint);
        boolean bInside = dResult < 0;
        dResult = Math.abs(dResult);

        for (int i = 0; i < Holes.size(); i++) {
            double dDist = Holes.get(i).Distance(TestPoint);
            if (dDist < 0)
                bInside = false;
            if (Math.abs(dDist) < dResult)
                dResult = Math.abs(dDist);
        }

        if (bInside)
            return dResult;
        else
            return -dResult;

    }

    /**
     * Distance from the line provided.
     *
     * @param Line Line to find the distance to.
     */
    public double Distance(C2DLineBase Line) {
        double dResult = Rim.Distance(Line);
        if (dResult == 0)
            return 0;

        boolean bInside = dResult < 0;
        dResult = Math.abs(dResult);

        for (int i = 0; i < Holes.size(); i++) {
            double dDist = Holes.get(i).Distance(Line);
            if (dDist == 0)
                return 0;


            if (dDist < 0)
                bInside = false;
            if (Math.abs(dDist) < dResult)
                dResult = Math.abs(dDist);
        }

        if (bInside)
            return dResult;
        else
            return -dResult;

    }

    /**
     * Distance from the polygon provided.
     *
     * @param Poly      Polygon to find the distance to.
     * @param ptOnThis  Closest point on this to recieve the result.
     * @param ptOnOther Closest point on the other to recieve the result.
     */
    public double Distance(C2DPolyBase Poly, C2DPoint ptOnThis, C2DPoint ptOnOther) {
        C2DPoint ptOnThisResult = new C2DPoint();
        C2DPoint ptOnOtherResult = new C2DPoint();


        double dResult = Rim.Distance(Poly, ptOnThis, ptOnOther);

        if (dResult == 0)
            return 0;

        ptOnThisResult.Set(ptOnThis);
        ptOnOtherResult.Set(ptOnOther);

        boolean bInside = dResult < 0;
        dResult = Math.abs(dResult);

        for (int i = 0; i < Holes.size(); i++) {
            double dDist = Holes.get(i).Distance(Poly, ptOnThis, ptOnOther);
            if (dDist == 0)
                return 0;


            if (dDist < 0)
                bInside = false;
            if (Math.abs(dDist) < dResult) {
                ptOnThisResult.Set(ptOnThis);
                ptOnOtherResult.Set(ptOnOther);

                dResult = Math.abs(dDist);
            }
        }

        ptOnThis.Set(ptOnThisResult);
        ptOnOther.Set(ptOnOtherResult);

        if (bInside)
            return dResult;
        else
            return -dResult;
    }

    /**
     * Proximity test.
     *
     * @param TestPoint Point to test against.
     * @param dDist     Distance threshold.
     */
    public boolean IsWithinDistance(C2DPoint TestPoint, double dDist) {
        if (Rim.IsWithinDistance(TestPoint, dDist))
            return true;

        for (int i = 0; i < Holes.size(); i++) {
            if (Holes.get(i).IsWithinDistance(TestPoint, dDist))
                return true;
        }

        return false;
    }

    /**
     * Perimeter.
     */
    public double GetPerimeter() {

        double dResult = Rim.GetPerimeter();

        for (int i = 0; i < Holes.size(); i++) {
            dResult += Holes.get(i).GetPerimeter();
        }

        return dResult;

    }

    /**
     * Projection onto the line.
     *
     * @param Line     Line to project this on.
     * @param Interval Interval to recieve the result.
     */
    public void Project(C2DLine Line, CInterval Interval) {
        Rim.Project(Line, Interval);
    }

    /**
     * Projection onto the vector.
     *
     * @param Vector   Vector to project this on.
     * @param Interval Interval to recieve the result.
     */
    public void Project(C2DVector Vector, CInterval Interval) {
        Rim.Project(Vector, Interval);
    }

    /**
     * Returns true if there are crossing lines.
     */
    public boolean HasCrossingLines() {
        C2DLineBaseSet Lines = new C2DLineBaseSet();

        Lines.addAll(0, Rim.Lines);

        for (int i = 0; i < Holes.size(); i++) {
            Lines.addAll(0, Holes.get(i).Lines);
        }

        return Lines.HasCrossingLines();
    }

    /**
     * Returns the bounding rectangle.
     *
     * @param Rect Rectangle to recieve the result.
     */
    public void GetBoundingRect(C2DRect Rect) {
        Rect.Set(Rim.BoundingRect);

        for (int i = 0; i < Holes.size(); i++) {
            Rect.ExpandToInclude(Holes.get(i).BoundingRect);
        }
    }

    /**
     * Point inside test.
     *
     * @param pt Point to test for.
     */
    public boolean Contains(C2DPoint pt) {
        if (!Rim.Contains(pt))
            return false;

        for (int i = 0; i < Holes.size(); i++) {
            if (Holes.get(i).Contains(pt))
                return false;
        }

        return true;
    }

    /**
     * Line entirely inside test.
     *
     * @param Line Line to test for.
     */
    public boolean Contains(C2DLineBase Line) {
        if (!Rim.Contains(Line))
            return false;

        for (int i = 0; i < Holes.size(); i++) {
            if (Holes.get(i).Crosses(Line) || Holes.get(i).Contains(Line.GetPointFrom()))
                return false;
        }

        return true;

    }

    /**
     * Polygon entirely inside test.
     *
     * @param Polygon Polygon to test for.
     */
    public boolean Contains(C2DPolyBase Polygon) {
        if (Rim == null)
            return false;

        if (!Rim.Contains(Polygon))
            return false;

        for (int i = 0; i < Holes.size(); i++) {
            if (Holes.get(i).Overlaps(Polygon))
                return false;
        }

        return true;
    }

    /**
     * Polygon entirely inside test.
     *
     * @param Polygon Polygon to test for.
     */
    public boolean Contains(C2DHoledPolyBase Polygon) {
        if (!Contains(Polygon.Rim))
            return false;

        for (int i = 0; i < Polygon.getHoleCount(); i++) {
            if (!Contains(Polygon.GetHole(i)))
                return false;
        }
        return true;
    }

    /**
     * True if this crosses the line
     *
     * @param Line Line to test for.
     */
    public boolean Crosses(C2DLineBase Line) {
        if (Rim.Crosses(Line))
            return true;

        for (int i = 0; i < Holes.size(); i++) {
            if (Holes.get(i).Crosses(Line))
                return true;
        }
        return false;
    }

    /**
     * True if this crosses the line.
     *
     * @param Line            Line to test for.
     * @param IntersectionPts Point set to recieve the intersections.
     */
    public boolean Crosses(C2DLineBase Line, C2DPointSet IntersectionPts) {
        C2DPointSet IntPts = new C2DPointSet();

        Rim.Crosses(Line, IntPts);

        for (int i = 0; i < Holes.size(); i++) {
            Holes.get(i).Crosses(Line, IntPts);
        }
        boolean bResult = (IntPts.size() != 0);
        IntersectionPts.ExtractAllOf(IntPts);

        return (bResult);

    }


    /**
     * True if this crosses the other polygon.
     *
     * @param Poly Polygon to test for.
     */
    public boolean Crosses(C2DPolyBase Poly) {
        if (Rim == null)
            return false;

        if (Rim.Crosses(Poly))
            return true;

        for (int i = 0; i < Holes.size(); i++) {
            if (Holes.get(i).Crosses(Poly))
                return true;
        }
        return false;
    }

    /**
     * True if this crosses the ray, returns the intersection points.
     *
     * @param Ray             Ray to test for.
     * @param IntersectionPts Intersection points.
     */
    public boolean CrossesRay(C2DLine Ray, C2DPointSet IntersectionPts) {
        C2DPointSet IntPts = new C2DPointSet();

        Rim.CrossesRay(Ray, IntPts);

        IntersectionPts.ExtractAllOf(IntPts);

        for (int i = 0; i < Holes.size(); i++) {
            if (Holes.get(i).CrossesRay(Ray, IntPts)) {
                double dDist = Ray.point.Distance(IntPts.get(0));
                int nInsert = 0;

                while (nInsert < IntersectionPts.size() &&
                        Ray.point.Distance(IntersectionPts.get(nInsert)) < dDist) {
                    nInsert++;
                }

                IntersectionPts.addAll(nInsert, IntPts);
            }
        }

        return (IntersectionPts.size() > 0);

    }

    /**
     * True if this overlaps the other.
     *
     * @param Other Other polygon to test for.
     */
    public boolean Overlaps(C2DHoledPolyBase Other) {
        if (!Overlaps(Other.Rim))
            return false;
        for (int i = 0; i < Other.getHoleCount(); i++) {
            if (Other.GetHole(i).Contains(Rim))
                return false;
        }
        return true;
    }

    /**
     * True if this overlaps the other.
     *
     * @param Other Other polygon to test for.
     */
    public boolean Overlaps(C2DPolyBase Other) {
        if (Rim == null)
            return false;

        if (!Rim.Overlaps(Other))
            return false;

        for (int i = 0; i < Holes.size(); i++) {
            if (Holes.get(i).Contains(Other))
                return false;
        }
        return true;
    }

    /**
     * Function to convert polygons to complex polygons. Assigning holes to those that are contained.
     * The set of holed polygons will be filled from the set of simple polygons.
     *
     * @param HoledPolys Holed polygon set.
     * @param Polygons   Simple polygon set.
     */
    public static void PolygonsToHoledPolygons(ArrayList<C2DHoledPolyBase> HoledPolys,
                                               ArrayList<C2DPolyBase> Polygons) {
        ArrayList<C2DPolyBase> Unmatched = new ArrayList<C2DPolyBase>();
        ArrayList<C2DHoledPolyBase> NewHoledPolys = new ArrayList<C2DHoledPolyBase>();

        for (int i = Polygons.size() - 1; i >= 0; i--) {
            boolean bMatched = false;

            C2DPolyBase pPoly = Polygons.get(i);
            Polygons.remove(i);

            // Cycle through the newly created polygons to see if it's a hole.
            for (int p = 0; p < NewHoledPolys.size(); p++) {
                if (NewHoledPolys.get(p).Rim.Contains(pPoly.Lines.get(0).GetPointFrom())) {
                    NewHoledPolys.get(p).AddHole(pPoly);
                    bMatched = true;
                    break;
                }
            }
            // If its not then compare it to all the other unknowns.
            if (!bMatched) {
                int u = 0;

                boolean bKnownRim = false;

                while (u < Unmatched.size()) {
                    if (!bKnownRim && Unmatched.get(u).Contains(pPoly.Lines.get(0).GetPointFrom())) {
                        // This is a hole.
                        NewHoledPolys.add(new C2DHoledPolyBase());
                        NewHoledPolys.get(NewHoledPolys.size() - 1).Rim = Unmatched.get(u);
                        Unmatched.remove(u);
                        NewHoledPolys.get(NewHoledPolys.size() - 1).AddHole(pPoly);
                        bMatched = true;
                        break;

                    } else if (pPoly.Contains(Unmatched.get(u).Lines.get(0).GetPointFrom())) {
                        //	int nCount = OverlapPolygons->GetCount();
                        // This is a rim.
                        if (!bKnownRim) {
                            // If we haven't alreay worked this out then record that its a rim
                            // and set up the new polygon.
                            bKnownRim = true;
                            NewHoledPolys.add(new C2DHoledPolyBase());
                            NewHoledPolys.get(NewHoledPolys.size() - 1).Rim = pPoly;
                            NewHoledPolys.get(NewHoledPolys.size() - 1).AddHole(Unmatched.get(u));
                            Unmatched.remove(u);
                        } else {
                            // We already worked out this was a rim so it must be the last polygon.
                            NewHoledPolys.get(NewHoledPolys.size() - 1).AddHole(Unmatched.get(u));
                            Unmatched.remove(u);
                        }
                        // Record that its been matched.
                        bMatched = true;
                    } else {
                        // Only if there was no match do we increment the counter.
                        u++;
                    }
                }
            }

            if (!bMatched) {
                Unmatched.add(pPoly);
            }
        }

        for (int i = 0; i < Unmatched.size(); i++) {
            C2DHoledPolyBase NewHoled = new C2DHoledPolyBase();
            NewHoled.Rim = Unmatched.get(i);
            HoledPolys.add(NewHoled);
        }

        HoledPolys.addAll(NewHoledPolys);
    }

    /**
     * Returns the overlaps between this and the other complex polygon.
     *
     * @param Other      Other polygon.
     * @param HoledPolys Set to receieve all the resulting polygons.
     * @param grid       Grid containing the degenerate handling settings.
     */
    public void GetOverlaps(C2DHoledPolyBase Other, ArrayList<C2DHoledPolyBase> HoledPolys,
                            CGrid grid) {
        GetBoolean(Other, HoledPolys, true, true, grid);
    }

    /**
     * Returns the difference between this and the other polygon.
     *
     * @param Other      Other polygon.
     * @param HoledPolys Set to receieve all the resulting polygons.
     * @param grid       Grid containing the degenerate handling settings.
     */
    public void GetNonOverlaps(C2DHoledPolyBase Other, ArrayList<C2DHoledPolyBase> HoledPolys,
                               CGrid grid) {
        GetBoolean(Other, HoledPolys, false, true, grid);

    }

    /**
     * Returns the union of this and the other.
     *
     * @param Other      Other polygon.
     * @param HoledPolys Set to receieve all the resulting polygons.
     * @param grid       Grid containing the degenerate handling settings.
     */
    public void GetUnion(C2DHoledPolyBase Other, ArrayList<C2DHoledPolyBase> HoledPolys,
                         CGrid grid) {
        GetBoolean(Other, HoledPolys, false, false, grid);
    }


    /**
     * Returns the routes (multiple lines or part polygons) either inside or
     * outside the polygons provided. These are based on the intersections
     * of the 2 polygons e.g. the routes / part polygons of one inside or
     * outside the other.
     *
     * @param Poly1          The first polygon.
     *                       <param name="bP1RoutesInside">True if routes inside the second polygon are
     *                       required for the first polygon.</param>
     * @param Poly2          The second polygon.
     *                       <param name="bP2RoutesInside">True if routes inside the first polygon are
     *                       required for the second polygon.</param>
     * @param Routes1        Output. Set of lines for the first polygon.
     * @param Routes2        Output. Set of lines for the second polygon.
     * @param CompleteHoles1 Output. Complete holes for the first polygon.
     * @param CompleteHoles2 Output. Complete holes for the second polygon.
     * @param grid           Contains the degenerate handling settings.
     */
    public static void GetRoutes(C2DHoledPolyBase Poly1, boolean bP1RoutesInside,
                                 C2DHoledPolyBase Poly2, boolean bP2RoutesInside,
                                 C2DLineBaseSetSet Routes1, C2DLineBaseSetSet Routes2,
                                 ArrayList<C2DPolyBase> CompleteHoles1, ArrayList<C2DPolyBase> CompleteHoles2,
                                 CGrid grid) {

        if (Poly1.Rim.Lines.size() == 0 || Poly2.Rim.Lines.size() == 0) {
            //  Debug.Assert(false, "Polygon with no lines" );
            return;
        }

        C2DPointSet IntPointsTemp = new C2DPointSet();
        C2DPointSet IntPointsRim1 = new C2DPointSet();
        C2DPointSet IntPointsRim2 = new C2DPointSet();
        ArrayList<Integer> IndexesRim1 = new ArrayList<Integer>();
        ArrayList<Integer> IndexesRim2 = new ArrayList<Integer>();


        ArrayList<C2DPointSet> IntPoints1AllHoles = new ArrayList<C2DPointSet>();
        ArrayList<C2DPointSet> IntPoints2AllHoles = new ArrayList<C2DPointSet>();
        ArrayList<ArrayList<Integer>> Indexes1AllHoles = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> Indexes2AllHoles = new ArrayList<ArrayList<Integer>>();
        //    std::vector<C2DPointSet* > IntPoints1AllHoles, IntPoints2AllHoles;
        //   std::vector<CIndexSet*> Indexes1AllHoles, Indexes2AllHoles;

        int usP1Holes = Poly1.getHoleCount();
        int usP2Holes = Poly2.getHoleCount();

        // *** Rim Rim Intersections
        Poly1.Rim.Lines.GetIntersections(Poly2.Rim.Lines,
                IntPointsTemp, IndexesRim1, IndexesRim2,
                Poly1.Rim.BoundingRect, Poly2.Rim.BoundingRect);

        IntPointsRim1.AddCopy(IntPointsTemp);
        IntPointsRim2.ExtractAllOf(IntPointsTemp);

        // *** Rim Hole Intersections
        for (int i = 0; i < usP2Holes; i++) {
            // Debug.Assert(IntPointsTemp.size() == 0);

            IntPoints2AllHoles.add(new C2DPointSet());
            Indexes2AllHoles.add(new ArrayList<Integer>());

            if (Poly1.Rim.BoundingRect.Overlaps(Poly2.GetHole(i).BoundingRect)) {
                Poly1.Rim.Lines.GetIntersections(Poly2.GetHole(i).Lines,
                        IntPointsTemp, IndexesRim1, Indexes2AllHoles.get(i),
                        Poly1.Rim.BoundingRect, Poly2.GetHole(i).BoundingRect);

                IntPointsRim1.AddCopy(IntPointsTemp);
                IntPoints2AllHoles.get(i).ExtractAllOf(IntPointsTemp);
            }
        }
        // *** Rim Hole Intersections
        for (int j = 0; j < usP1Holes; j++) {
            //  Debug.Assert(IntPointsTemp.size() == 0);

            IntPoints1AllHoles.add(new C2DPointSet());
            Indexes1AllHoles.add(new ArrayList<Integer>());

            if (Poly2.Rim.BoundingRect.Overlaps(Poly1.GetHole(j).BoundingRect)) {
                Poly2.Rim.Lines.GetIntersections(Poly1.GetHole(j).Lines,
                        IntPointsTemp, IndexesRim2, Indexes1AllHoles.get(j),
                        Poly2.Rim.BoundingRect, Poly1.GetHole(j).BoundingRect);

                IntPointsRim2.AddCopy(IntPointsTemp);
                IntPoints1AllHoles.get(j).ExtractAllOf(IntPointsTemp);
            }

        }

        // *** Quick Escape
        boolean bRim1StartInPoly2 = Poly2.Contains(Poly1.Rim.Lines.get(0).GetPointFrom());
        boolean bRim2StartInPoly1 = Poly1.Contains(Poly2.Rim.Lines.get(0).GetPointFrom());

        if (IntPointsRim1.size() != 0 || IntPointsRim2.size() != 0 ||
                bRim1StartInPoly2 || bRim2StartInPoly1)
        // pos no interaction
        {
            // *** Rim Routes
            Poly1.Rim.GetRoutes(IntPointsRim1, IndexesRim1, Routes1,
                    bRim1StartInPoly2, bP1RoutesInside);
            Poly2.Rim.GetRoutes(IntPointsRim2, IndexesRim2, Routes2,
                    bRim2StartInPoly1, bP2RoutesInside);

            if (IntPointsRim1.size() % 2 != 0)    // Must be even
            {
                grid.LogDegenerateError();
                //  Debug.Assert(false);
            }

            if (IntPointsRim2.size() % 2 != 0)    // Must be even
            {
                grid.LogDegenerateError();
                assert false;
            }

            // *** Hole Hole Intersections
            for (int h = 0; h < usP1Holes; h++) {
                for (int k = 0; k < usP2Holes; k++) {
                    assert IntPointsTemp.size() == 0;
                    C2DPolyBase pHole1 = Poly1.GetHole(h);
                    C2DPolyBase pHole2 = Poly2.GetHole(k);

                    if (pHole1.BoundingRect.Overlaps(pHole2.BoundingRect)) {
                        pHole1.Lines.GetIntersections(pHole2.Lines,
                                IntPointsTemp, Indexes1AllHoles.get(h), Indexes2AllHoles.get(k),
                                pHole1.BoundingRect, pHole2.BoundingRect);

                        IntPoints1AllHoles.get(h).AddCopy(IntPointsTemp);
                        IntPoints2AllHoles.get(k).ExtractAllOf(IntPointsTemp);
                    }
                }
            }


            // *** Hole Routes
            for (int a = 0; a < usP1Holes; a++) {
                C2DPolyBase pHole = Poly1.GetHole(a);

                if (IntPoints1AllHoles.get(a).size() % 2 != 0)    // Must be even
                {
                    grid.LogDegenerateError();
                    assert false;
                }

                if (pHole.Lines.size() != 0) {
                    boolean bHole1StartInside = Poly2.Contains(pHole.Lines.get(0).GetPointFrom());
                    if (IntPoints1AllHoles.get(a).size() == 0) {
                        if (bHole1StartInside == bP1RoutesInside)
                            CompleteHoles1.add(new C2DPolyBase(pHole));
                    } else {
                        pHole.GetRoutes(IntPoints1AllHoles.get(a), Indexes1AllHoles.get(a), Routes1,
                                bHole1StartInside, bP1RoutesInside);
                    }
                }
            }
            // *** Hole Routes
            for (int b = 0; b < usP2Holes; b++) {
                C2DPolyBase pHole = Poly2.GetHole(b);

                if (IntPoints2AllHoles.get(b).size() % 2 != 0)    // Must be even
                {
                    grid.LogDegenerateError();
                    //    Debug.Assert(false);
                }

                if (pHole.Lines.size() != 0) {
                    boolean bHole2StartInside = Poly1.Contains(pHole.Lines.get(0).GetPointFrom());
                    if (IntPoints2AllHoles.get(b).size() == 0) {
                        if (bHole2StartInside == bP2RoutesInside)
                            CompleteHoles2.add(new C2DPolyBase(pHole));
                    } else {
                        pHole.GetRoutes(IntPoints2AllHoles.get(b), Indexes2AllHoles.get(b), Routes2,
                                bHole2StartInside, bP2RoutesInside);
                    }
                }
            }
        }


        //for (unsigned int i = 0 ; i < IntPoints1AllHoles.size(); i++)
        //    delete IntPoints1AllHoles.get(i);
        //for (unsigned int i = 0 ; i < IntPoints2AllHoles.size(); i++)
        //    delete IntPoints2AllHoles.get(i);
        //for (unsigned int i = 0 ; i < Indexes1AllHoles.size(); i++)
        //    delete Indexes1AllHoles.get(i);
        //for (unsigned int i = 0 ; i < Indexes2AllHoles.size(); i++)
        //    delete Indexes2AllHoles.get(i);

    }

    /**
     * Moves this by a small random amount.
     */
    public void RandomPerturb() {
        C2DPoint pt = Rim.BoundingRect.GetPointFurthestFromOrigin();
        double dMinEq = Math.max(pt.x, pt.y) * Constants.conEqualityTolerance;
        CRandomNumber rn = new CRandomNumber(dMinEq * 10, dMinEq * 100);

        C2DVector cVector = new C2DVector(rn.Get(), rn.Get());
        if (rn.GetBool())
            cVector.i = -cVector.i;
        if (rn.GetBool())
            cVector.j = -cVector.j;

        Move(cVector);

    }

    /**
     * Snaps this to the conceptual grip.
     *
     * @param grid The grid to snap to.
     */
    public void SnapToGrid(CGrid grid) {
        Rim.SnapToGrid(grid);

        for (int i = 0; i < Holes.size(); i++) {
            GetHole(i).SnapToGrid(grid);
        }
    }

    /**
     * Returns the boolean result (e.g. union) of 2 shapes. Boolean Operation defined by
     * the inside / outside flags.
     *
     * @param Other        Other polygon.
     * @param HoledPolys   Set of polygons to recieve the result.
     * @param bThisInside  Does the operation require elements of this INSIDE the other.
     * @param bOtherInside Does the operation require elements of the other INSIDE this.
     * @param grid         The grid with the degenerate settings.
     */
    public void GetBoolean(C2DHoledPolyBase Other, ArrayList<C2DHoledPolyBase> HoledPolys,
                           boolean bThisInside, boolean bOtherInside,
                           CGrid grid) {
        if (Rim.Lines.size() == 0 || Other.Rim.Lines.size() == 0)
            return;

        if (Rim.BoundingRect.Overlaps(Other.Rim.BoundingRect)) {
            switch (grid.DegenerateHandling) {
                case None: {
                    ArrayList<C2DPolyBase> CompleteHoles1 = new ArrayList<C2DPolyBase>();
                    ArrayList<C2DPolyBase> CompleteHoles2 = new ArrayList<C2DPolyBase>();
                    C2DLineBaseSetSet Routes1 = new C2DLineBaseSetSet();
                    C2DLineBaseSetSet Routes2 = new C2DLineBaseSetSet();
                    GetRoutes(this, bThisInside, Other, bOtherInside, Routes1, Routes2,
                            CompleteHoles1, CompleteHoles2, grid);

                    Routes1.ExtractAllOf(Routes2);

                    if (Routes1.size() > 0) {
                        Routes1.MergeJoining();

                        ArrayList<C2DPolyBase> Polygons = new ArrayList<C2DPolyBase>();

                        for (int i = Routes1.size() - 1; i >= 0; i--) {
                            C2DLineBaseSet pRoute = Routes1.get(i);
                            if (pRoute.IsClosed(true)) {
                                Polygons.add(new C2DPolyBase());
                                Polygons.get(Polygons.size() - 1).CreateDirect(pRoute);
                            } else {
                                assert false;
                                grid.LogDegenerateError();
                            }
                        }

                        C2DHoledPolyBaseSet NewComPolys = new C2DHoledPolyBaseSet();

                        PolygonsToHoledPolygons(NewComPolys, Polygons);

                        NewComPolys.AddKnownHoles(CompleteHoles1);

                        NewComPolys.AddKnownHoles(CompleteHoles2);

                        if (!bThisInside && !bOtherInside && NewComPolys.size() != 1) {
                            //  Debug.Assert(false);
                            grid.LogDegenerateError();
                        }


                        HoledPolys.addAll(NewComPolys);

                        NewComPolys.clear();
                    }
                }
                break;
                case RandomPerturbation: {
                    C2DHoledPolyBase OtherCopy = new C2DHoledPolyBase(Other);
                    OtherCopy.RandomPerturb();
                    grid.DegenerateHandling = CGrid.eDegenerateHandling.None;
                    GetBoolean(OtherCopy, HoledPolys, bThisInside, bOtherInside, grid);
                    grid.DegenerateHandling = CGrid.eDegenerateHandling.RandomPerturbation;
                }
                break;
                case DynamicGrid: {
                    C2DRect Rect = new C2DRect();
                    if (Rim.BoundingRect.Overlaps(Other.Rim.BoundingRect, Rect)) {
                        //double dOldGrid = CGrid::GetGridSize();
                        grid.SetToMinGridSize(Rect, false);
                        grid.DegenerateHandling = CGrid.eDegenerateHandling.PreDefinedGrid;
                        GetBoolean(Other, HoledPolys, bThisInside, bOtherInside, grid);
                        grid.DegenerateHandling = CGrid.eDegenerateHandling.DynamicGrid;
                    }
                }
                break;
                case PreDefinedGrid: {
                    C2DHoledPolyBase P1 = new C2DHoledPolyBase(this);
                    C2DHoledPolyBase P2 = new C2DHoledPolyBase(Other);
                    P1.SnapToGrid(grid);
                    P2.SnapToGrid(grid);
                    C2DVector V1 = new C2DVector(P1.Rim.BoundingRect.getTopLeft(), P2.Rim.BoundingRect.getTopLeft());
                    double dPerturbation = grid.getGridSize(); // ensure it snaps back to original grid positions.
                    if (V1.i > 0)
                        V1.i = dPerturbation;
                    else
                        V1.i = -dPerturbation;    // move away slightly if possible
                    if (V1.j > 0)
                        V1.j = dPerturbation;
                    else
                        V1.j = -dPerturbation; // move away slightly if possible
                    V1.i *= 0.411923;// ensure it snaps back to original grid positions.
                    V1.j *= 0.313131;// ensure it snaps back to original grid positions.

                    P2.Move(V1);
                    grid.DegenerateHandling = CGrid.eDegenerateHandling.None;
                    P1.GetBoolean(P2, HoledPolys, bThisInside, bOtherInside, grid);

                    for (int i = 0; i < HoledPolys.size(); i++)
                        HoledPolys.get(i).SnapToGrid(grid);

                    grid.DegenerateHandling = CGrid.eDegenerateHandling.PreDefinedGrid;

                }
                break;
                case PreDefinedGridPreSnapped: {
                    C2DHoledPolyBase P2 = new C2DHoledPolyBase(Other);
                    C2DVector V1 = new C2DVector(Rim.BoundingRect.getTopLeft(), P2.Rim.BoundingRect.getTopLeft());
                    double dPerturbation = grid.getGridSize();
                    if (V1.i > 0)
                        V1.i = dPerturbation;
                    else
                        V1.i = -dPerturbation; // move away slightly if possible
                    if (V1.j > 0)
                        V1.j = dPerturbation;
                    else
                        V1.j = -dPerturbation; // move away slightly if possible
                    V1.i *= 0.411923; // ensure it snaps back to original grid positions.
                    V1.j *= 0.313131;// ensure it snaps back to original grid positions.
                    P2.Move(V1);

                    grid.DegenerateHandling = CGrid.eDegenerateHandling.None;
                    GetBoolean(P2, HoledPolys, bThisInside, bOtherInside, grid);

                    for (int i = 0; i < HoledPolys.size(); i++)
                        HoledPolys.get(i).SnapToGrid(grid);

                    grid.DegenerateHandling = CGrid.eDegenerateHandling.PreDefinedGridPreSnapped;
                }
                break;
            }// switch
        }
    }

    /**
     * Transform by a user defined transformation. e.g. a projection.
     */
    public void Transform(CTransformation pProject) {
        if (Rim != null)
            Rim.Transform(pProject);

        for (int i = 0; i < Holes.size(); i++) {
            Holes.get(i).Transform(pProject);
        }
    }

    /**
     * Transform by a user defined transformation. e.g. a projection.
     */
    public void InverseTransform(CTransformation pProject) {
        if (Rim != null)
            Rim.InverseTransform(pProject);

        for (int i = 0; i < Holes.size(); i++) {
            Holes.get(i).Transform(pProject);
        }
    }


    /**
     * True if this overlaps the rect.
     */
    public boolean Overlaps(C2DRect rect) {
        if (Rim == null)
            return false;

        if (!Rim.Overlaps(rect))
            return false;

        for (int i = 0; i < Holes.size(); i++) {
            if (Holes.get(i).Contains(rect))
                return false;
        }
        return true;
    }

    /**
     * Polygon entirely inside test.
     */
    public boolean Contains(C2DRect rect) {
        if (Rim == null)
            return false;

        if (!Rim.Contains(rect))
            return false;

        for (int i = 0; i < Holes.size(); i++) {
            if (Holes.get(i).Overlaps(rect))
                return false;
        }

        return true;
    }


    /**
     * True if this crosses the other rect.
     */
    public boolean Crosses(C2DRect rect) {
        if (Rim == null)
            return false;

        if (Rim.Crosses(rect))
            return true;

        for (int i = 0; i < Holes.size(); i++) {
            if (Holes.get(i).Crosses(rect))
                return true;
        }
        return false;
    }


    /**
     * True if this is contained by the rect.
     */
    public boolean IsContainedBy(C2DRect rect) {
        if (Rim == null)
            return false;

        return Rim.IsContainedBy(rect);
    }


    /**
     * The outer rim.
     */
    protected C2DPolyBase Rim = null;

    /**
     * Rim access.
     */
    public C2DPolyBase getRim() {
        return Rim;
    }

    /**
     * Rim access.
     */
    public void setRim(C2DPolyBase rim) {
        Rim = rim;
    }


    /**
     * Holes.
     */
    protected ArrayList<C2DPolyBase> Holes = new ArrayList<C2DPolyBase>();

    /**
     * Hole count.
     */
    public int getHoleCount() {
        return Holes.size();
    }

    /**
     * Hole access.
     */
    public C2DPolyBase GetHole(int i) {
        return Holes.get(i);
    }

    /**
     * Hole assignment.
     */
    public void SetHole(int i, C2DPolyBase Poly) {
        Holes.set(i, Poly);
    }

    /**
     * Hole addition.
     */
    public void AddHole(C2DPolyBase Poly) {
        Holes.add(Poly);
    }

    /**
     * Hole removal.
     */
    public void RemoveHole(int i) {
        Holes.remove(i);
    }

}