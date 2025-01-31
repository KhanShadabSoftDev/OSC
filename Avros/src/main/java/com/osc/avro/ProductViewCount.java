/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.osc.avro;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class ProductViewCount extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -3440522117485248217L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"ProductViewCount\",\"namespace\":\"com.osc.avro\",\"fields\":[{\"name\":\"productId\",\"type\":\"string\"},{\"name\":\"categoryId\",\"type\":\"string\"},{\"name\":\"count\",\"type\":\"long\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<ProductViewCount> ENCODER =
      new BinaryMessageEncoder<ProductViewCount>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<ProductViewCount> DECODER =
      new BinaryMessageDecoder<ProductViewCount>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<ProductViewCount> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<ProductViewCount> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<ProductViewCount> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<ProductViewCount>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this ProductViewCount to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a ProductViewCount from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a ProductViewCount instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static ProductViewCount fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private java.lang.CharSequence productId;
   private java.lang.CharSequence categoryId;
   private long count;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public ProductViewCount() {}

  /**
   * All-args constructor.
   * @param productId The new value for productId
   * @param categoryId The new value for categoryId
   * @param count The new value for count
   */
  public ProductViewCount(java.lang.CharSequence productId, java.lang.CharSequence categoryId, java.lang.Long count) {
    this.productId = productId;
    this.categoryId = categoryId;
    this.count = count;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return productId;
    case 1: return categoryId;
    case 2: return count;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: productId = (java.lang.CharSequence)value$; break;
    case 1: categoryId = (java.lang.CharSequence)value$; break;
    case 2: count = (java.lang.Long)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'productId' field.
   * @return The value of the 'productId' field.
   */
  public java.lang.CharSequence getProductId() {
    return productId;
  }


  /**
   * Sets the value of the 'productId' field.
   * @param value the value to set.
   */
  public void setProductId(java.lang.CharSequence value) {
    this.productId = value;
  }

  /**
   * Gets the value of the 'categoryId' field.
   * @return The value of the 'categoryId' field.
   */
  public java.lang.CharSequence getCategoryId() {
    return categoryId;
  }


  /**
   * Sets the value of the 'categoryId' field.
   * @param value the value to set.
   */
  public void setCategoryId(java.lang.CharSequence value) {
    this.categoryId = value;
  }

  /**
   * Gets the value of the 'count' field.
   * @return The value of the 'count' field.
   */
  public long getCount() {
    return count;
  }


  /**
   * Sets the value of the 'count' field.
   * @param value the value to set.
   */
  public void setCount(long value) {
    this.count = value;
  }

  /**
   * Creates a new ProductViewCount RecordBuilder.
   * @return A new ProductViewCount RecordBuilder
   */
  public static com.osc.avro.ProductViewCount.Builder newBuilder() {
    return new com.osc.avro.ProductViewCount.Builder();
  }

  /**
   * Creates a new ProductViewCount RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new ProductViewCount RecordBuilder
   */
  public static com.osc.avro.ProductViewCount.Builder newBuilder(com.osc.avro.ProductViewCount.Builder other) {
    if (other == null) {
      return new com.osc.avro.ProductViewCount.Builder();
    } else {
      return new com.osc.avro.ProductViewCount.Builder(other);
    }
  }

  /**
   * Creates a new ProductViewCount RecordBuilder by copying an existing ProductViewCount instance.
   * @param other The existing instance to copy.
   * @return A new ProductViewCount RecordBuilder
   */
  public static com.osc.avro.ProductViewCount.Builder newBuilder(com.osc.avro.ProductViewCount other) {
    if (other == null) {
      return new com.osc.avro.ProductViewCount.Builder();
    } else {
      return new com.osc.avro.ProductViewCount.Builder(other);
    }
  }

  /**
   * RecordBuilder for ProductViewCount instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<ProductViewCount>
    implements org.apache.avro.data.RecordBuilder<ProductViewCount> {

    private java.lang.CharSequence productId;
    private java.lang.CharSequence categoryId;
    private long count;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.osc.avro.ProductViewCount.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.productId)) {
        this.productId = data().deepCopy(fields()[0].schema(), other.productId);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.categoryId)) {
        this.categoryId = data().deepCopy(fields()[1].schema(), other.categoryId);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.count)) {
        this.count = data().deepCopy(fields()[2].schema(), other.count);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
    }

    /**
     * Creates a Builder by copying an existing ProductViewCount instance
     * @param other The existing instance to copy.
     */
    private Builder(com.osc.avro.ProductViewCount other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.productId)) {
        this.productId = data().deepCopy(fields()[0].schema(), other.productId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.categoryId)) {
        this.categoryId = data().deepCopy(fields()[1].schema(), other.categoryId);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.count)) {
        this.count = data().deepCopy(fields()[2].schema(), other.count);
        fieldSetFlags()[2] = true;
      }
    }

    /**
      * Gets the value of the 'productId' field.
      * @return The value.
      */
    public java.lang.CharSequence getProductId() {
      return productId;
    }


    /**
      * Sets the value of the 'productId' field.
      * @param value The value of 'productId'.
      * @return This builder.
      */
    public com.osc.avro.ProductViewCount.Builder setProductId(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.productId = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'productId' field has been set.
      * @return True if the 'productId' field has been set, false otherwise.
      */
    public boolean hasProductId() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'productId' field.
      * @return This builder.
      */
    public com.osc.avro.ProductViewCount.Builder clearProductId() {
      productId = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'categoryId' field.
      * @return The value.
      */
    public java.lang.CharSequence getCategoryId() {
      return categoryId;
    }


    /**
      * Sets the value of the 'categoryId' field.
      * @param value The value of 'categoryId'.
      * @return This builder.
      */
    public com.osc.avro.ProductViewCount.Builder setCategoryId(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.categoryId = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'categoryId' field has been set.
      * @return True if the 'categoryId' field has been set, false otherwise.
      */
    public boolean hasCategoryId() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'categoryId' field.
      * @return This builder.
      */
    public com.osc.avro.ProductViewCount.Builder clearCategoryId() {
      categoryId = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'count' field.
      * @return The value.
      */
    public long getCount() {
      return count;
    }


    /**
      * Sets the value of the 'count' field.
      * @param value The value of 'count'.
      * @return This builder.
      */
    public com.osc.avro.ProductViewCount.Builder setCount(long value) {
      validate(fields()[2], value);
      this.count = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'count' field has been set.
      * @return True if the 'count' field has been set, false otherwise.
      */
    public boolean hasCount() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'count' field.
      * @return This builder.
      */
    public com.osc.avro.ProductViewCount.Builder clearCount() {
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ProductViewCount build() {
      try {
        ProductViewCount record = new ProductViewCount();
        record.productId = fieldSetFlags()[0] ? this.productId : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.categoryId = fieldSetFlags()[1] ? this.categoryId : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.count = fieldSetFlags()[2] ? this.count : (java.lang.Long) defaultValue(fields()[2]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<ProductViewCount>
    WRITER$ = (org.apache.avro.io.DatumWriter<ProductViewCount>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<ProductViewCount>
    READER$ = (org.apache.avro.io.DatumReader<ProductViewCount>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.productId);

    out.writeString(this.categoryId);

    out.writeLong(this.count);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.productId = in.readString(this.productId instanceof Utf8 ? (Utf8)this.productId : null);

      this.categoryId = in.readString(this.categoryId instanceof Utf8 ? (Utf8)this.categoryId : null);

      this.count = in.readLong();

    } else {
      for (int i = 0; i < 3; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.productId = in.readString(this.productId instanceof Utf8 ? (Utf8)this.productId : null);
          break;

        case 1:
          this.categoryId = in.readString(this.categoryId instanceof Utf8 ? (Utf8)this.categoryId : null);
          break;

        case 2:
          this.count = in.readLong();
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










