package com.esri.udt

import com.esri.core.geometry.Geometry

/**
  * PolylineType
  *
  * @param xyNum each element contains the number of xy pairs to read for a part
  * @param xyArr sequence of xy elements
  */
class PolylineType(override val xmin: Double,
                   override val ymin: Double,
                   override val xmax: Double,
                   override val ymax: Double,
                   override val xyNum: Array[Int],
                   override val xyArr: Array[Double]
                  ) extends PolyType(xmin, ymin, xmax, ymax, xyNum, xyArr) {

  @transient override lazy val asGeometry: Geometry = ???

  override def equals(other: Any): Boolean = other match {
    case that: PolylineType => equalsType(that)
    case _ => false
  }
}

object PolylineType {
  def apply(xmin: Double,
            ymin: Double,
            xmax: Double,
            ymax: Double,
            xyNum: Array[Int],
            xyArr: Array[Double]
           ) = {
    new PolylineType(xmin, ymin, xmax, ymax, xyNum, xyArr)
  }

  def unapply(p: PolylineType) =
    Some((p.xmin, p.ymin, p.xmax, p.ymax, p.xyNum, p.xyArr))
}