// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package tcssim

import cats.syntax.all._
import gov.aps.jca.dbr._
import lucuma.core.util.Enumerated
import tcssim.epics.MemoryPV.ToDBRType

import scala.reflect.ClassTag

package object epics {

  implicit val byteDBRType: ToDBRType[Byte] = new ToDBRType[Byte] {
    override type Dbr = DBR_Byte
    override val dbrType: DBRType                             = DBRType.BYTE
    override def buildDBR(): DBR_TIME_Byte                    = new DBR_TIME_Byte()
    override def toEpicsVal(v: Array[Byte]): DBR_TIME_Byte    = new DBR_TIME_Byte(v)
    override def fromEpicsValue(v: DBR_Byte): Array[Byte]     = v.getByteValue
    override def extractOption(dbr: DBR): Option[Array[Byte]] = dbr match {
      case x: DBR_TIME_Byte => fromEpicsValue(x).some
      case _                => none
    }
  }

  implicit val shortDBRType: ToDBRType[Short] = new ToDBRType[Short] {
    override type Dbr = DBR_Short
    override val dbrType: DBRType                              = DBRType.SHORT
    override def buildDBR(): DBR_TIME_Short                    = new DBR_TIME_Short()
    override def toEpicsVal(v: Array[Short]): DBR_TIME_Short   = new DBR_TIME_Short(v)
    override def fromEpicsValue(v: DBR_Short): Array[Short]    = v.getShortValue
    override def extractOption(dbr: DBR): Option[Array[Short]] = dbr match {
      case x: DBR_TIME_Short => fromEpicsValue(x).some
      case _                 => none
    }
  }

  implicit val intDBRType: ToDBRType[Int] = new ToDBRType[Int] {
    override type Dbr = DBR_Int
    override val dbrType: DBRType                            = DBRType.INT
    override def buildDBR(): DBR_TIME_Int                    = new DBR_TIME_Int()
    override def toEpicsVal(v: Array[Int]): DBR_TIME_Int     = new DBR_TIME_Int(v)
    override def fromEpicsValue(v: DBR_Int): Array[Int]      = v.getIntValue
    override def extractOption(dbr: DBR): Option[Array[Int]] = dbr match {
      case x: DBR_TIME_Int => fromEpicsValue(x).some
      case _               => none
    }
  }

  implicit val floatDBRType: ToDBRType[Float] = new ToDBRType[Float] {
    override type Dbr = DBR_Float
    override val dbrType: DBRType                              = DBRType.FLOAT
    override def buildDBR(): DBR_TIME_Float                    = new DBR_TIME_Float()
    override def toEpicsVal(v: Array[Float]): DBR_TIME_Float   = new DBR_TIME_Float(v)
    override def fromEpicsValue(v: DBR_Float): Array[Float]    = v.getFloatValue
    override def extractOption(dbr: DBR): Option[Array[Float]] = dbr match {
      case x: DBR_TIME_Float => fromEpicsValue(x).some
      case _                 => none
    }
  }

  implicit val doubleDBRType: ToDBRType[Double] = new ToDBRType[Double] {
    override type Dbr = DBR_Double
    override val dbrType: DBRType                               = DBRType.DOUBLE
    override def buildDBR(): DBR_TIME_Double                    = new DBR_TIME_Double()
    override def toEpicsVal(v: Array[Double]): DBR_TIME_Double  = new DBR_TIME_Double(v)
    override def fromEpicsValue(v: DBR_Double): Array[Double]   = v.getDoubleValue
    override def extractOption(dbr: DBR): Option[Array[Double]] = dbr match {
      case x: DBR_TIME_Double => fromEpicsValue(x).some
      case _                  => none
    }
  }

  implicit val stringDBRType: ToDBRType[String] = new ToDBRType[String] {
    override type Dbr = DBR_String
    override val dbrType: DBRType                               = DBRType.STRING
    override def buildDBR(): DBR_TIME_String                    = new DBR_TIME_String()
    override def toEpicsVal(v: Array[String]): DBR_TIME_String  = new DBR_TIME_String(v)
    override def fromEpicsValue(v: DBR_String): Array[String]   = v.getStringValue
    override def extractOption(dbr: DBR): Option[Array[String]] = dbr match {
      case x: DBR_TIME_String => fromEpicsValue(x).some
      case _                  => none
    }
  }

  implicit def enumeratedDBRType[T: Enumerated: ClassTag]: ToDBRType[T] = new ToDBRType[T] {
    private val enumerated = Enumerated[T]
    override type Dbr = DBR_Enum
    override val dbrType: DBRType                              = DBRType.ENUM
    override def buildDBR(): DBR_TIME_LABELS_Enum              = new DBR_TIME_LABELS_Enum()
    override def toEpicsVal(v: Array[T]): DBR_TIME_LABELS_Enum = new DBR_TIME_LABELS_Enum(
      v.map(enumerated.all.indexOf(_).toShort)
    )
    override def fromEpicsValue(v: DBR_Enum): Array[T]         =
      v.getEnumValue.map(i => enumerated.all(i.toInt))
    override def extractOption(dbr: DBR): Option[Array[T]]     = dbr match {
      case x: DBR_TIME_LABELS_Enum => fromEpicsValue(x).some
      case _                       => none
    }
    override def initValue(v: Array[T]): AnyRef                = v.map(enumerated.all.indexOf(_).toShort)
    override val enumLabels: Array[String]                     = enumerated.all.map(enumerated.tag).toArray
  }

}
