package com.esri.gdb

import java.nio.ByteBuffer

import com.esri.udt.{PolylineType, PolylineUDT}
import org.apache.spark.sql.types.Metadata

/**
  */
object FieldPolylineType extends Serializable {
  def apply(name: String,
            nullValueAllowed: Boolean,
            xOrig: Double,
            yOrig: Double,
            xyScale: Double,
            metadata: Metadata) = {
    new FieldPolylineType(name, nullValueAllowed, xOrig, yOrig, xyScale, metadata)
  }

}

class FieldPolylineType(name: String,
                        nullValueAllowed: Boolean,
                        xOrig: Double,
                        yOrig: Double,
                        xyScale: Double,
                        metadata: Metadata
                       )
  extends FieldGeom(name, new PolylineUDT(), nullValueAllowed, xOrig, yOrig, xyScale, metadata) {

  override def readValue(byteBuffer: ByteBuffer, oid: Int) = {
    val blob = getByteBuffer(byteBuffer)

    val geomType = blob getVarUInt

    val numPoints = blob.getVarUInt.toInt
    val numParts = blob.getVarUInt.toInt

    val xmin = blob.getVarUInt / xyScale + xOrig
    val ymin = blob.getVarUInt / xyScale + yOrig
    val xmax = blob.getVarUInt / xyScale + xmin
    val ymax = blob.getVarUInt / xyScale + ymin

    var dx = 0L
    var dy = 0L

    val xyNum = new Array[Int](numParts)
    val xyArr = new Array[Double](numPoints * 2)

    if (numParts > 1) {
      var i = 0
      var sum = 0
      1 to numParts foreach (partIndex => {
        if (partIndex == numParts) {
          xyNum(i) = numPoints - sum
        } else {
          val numXY = blob.getVarUInt.toInt
          xyNum(i) = numXY
          sum += numXY
          i += 1
        }
      })
      xyNum.foreach(numXY => {
        0 until numXY foreach (n => {
          dx += blob.getVarInt
          dy += blob.getVarInt
          val x = dx / xyScale + xOrig
          val y = dy / xyScale + yOrig
          xyArr(i) = x
          i += 1
          xyArr(i) = y
          i += 1
        })
      })
    }
    else {
      xyNum(0) = numPoints
      var i = 0
      0 until numPoints foreach (n => {
        dx += blob.getVarInt
        dy += blob.getVarInt
        val x = dx / xyScale + xOrig
        val y = dy / xyScale + yOrig
        xyArr(i) = x
        i += 1
        xyArr(i) = y
        i += 1
      })
    }

    PolylineType(xmin, ymin, xmax, ymax, xyNum, xyArr)
  }
}
