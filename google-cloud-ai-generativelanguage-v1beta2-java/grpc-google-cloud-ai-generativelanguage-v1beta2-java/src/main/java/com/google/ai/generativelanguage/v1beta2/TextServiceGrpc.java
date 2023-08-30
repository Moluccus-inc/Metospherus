package com.google.ai.generativelanguage.v1beta2;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * API for using Generative Language Models (GLMs) trained to generate text.
 * Also known as Large Language Models (LLM)s, these generate text given an
 * input prompt from the user.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler",
    comments = "Source: google/ai/generativelanguage/v1beta2/text_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class TextServiceGrpc {

  private TextServiceGrpc() {}

  public static final String SERVICE_NAME = "google.ai.generativelanguage.v1beta2.TextService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.google.ai.generativelanguage.v1beta2.GenerateTextRequest,
      com.google.ai.generativelanguage.v1beta2.GenerateTextResponse> getGenerateTextMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GenerateText",
      requestType = com.google.ai.generativelanguage.v1beta2.GenerateTextRequest.class,
      responseType = com.google.ai.generativelanguage.v1beta2.GenerateTextResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.ai.generativelanguage.v1beta2.GenerateTextRequest,
      com.google.ai.generativelanguage.v1beta2.GenerateTextResponse> getGenerateTextMethod() {
    io.grpc.MethodDescriptor<com.google.ai.generativelanguage.v1beta2.GenerateTextRequest, com.google.ai.generativelanguage.v1beta2.GenerateTextResponse> getGenerateTextMethod;
    if ((getGenerateTextMethod = TextServiceGrpc.getGenerateTextMethod) == null) {
      synchronized (TextServiceGrpc.class) {
        if ((getGenerateTextMethod = TextServiceGrpc.getGenerateTextMethod) == null) {
          TextServiceGrpc.getGenerateTextMethod = getGenerateTextMethod =
              io.grpc.MethodDescriptor.<com.google.ai.generativelanguage.v1beta2.GenerateTextRequest, com.google.ai.generativelanguage.v1beta2.GenerateTextResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GenerateText"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.ai.generativelanguage.v1beta2.GenerateTextRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.ai.generativelanguage.v1beta2.GenerateTextResponse.getDefaultInstance()))
              .setSchemaDescriptor(new TextServiceMethodDescriptorSupplier("GenerateText"))
              .build();
        }
      }
    }
    return getGenerateTextMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.ai.generativelanguage.v1beta2.EmbedTextRequest,
      com.google.ai.generativelanguage.v1beta2.EmbedTextResponse> getEmbedTextMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "EmbedText",
      requestType = com.google.ai.generativelanguage.v1beta2.EmbedTextRequest.class,
      responseType = com.google.ai.generativelanguage.v1beta2.EmbedTextResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.ai.generativelanguage.v1beta2.EmbedTextRequest,
      com.google.ai.generativelanguage.v1beta2.EmbedTextResponse> getEmbedTextMethod() {
    io.grpc.MethodDescriptor<com.google.ai.generativelanguage.v1beta2.EmbedTextRequest, com.google.ai.generativelanguage.v1beta2.EmbedTextResponse> getEmbedTextMethod;
    if ((getEmbedTextMethod = TextServiceGrpc.getEmbedTextMethod) == null) {
      synchronized (TextServiceGrpc.class) {
        if ((getEmbedTextMethod = TextServiceGrpc.getEmbedTextMethod) == null) {
          TextServiceGrpc.getEmbedTextMethod = getEmbedTextMethod =
              io.grpc.MethodDescriptor.<com.google.ai.generativelanguage.v1beta2.EmbedTextRequest, com.google.ai.generativelanguage.v1beta2.EmbedTextResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "EmbedText"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.ai.generativelanguage.v1beta2.EmbedTextRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.ai.generativelanguage.v1beta2.EmbedTextResponse.getDefaultInstance()))
              .setSchemaDescriptor(new TextServiceMethodDescriptorSupplier("EmbedText"))
              .build();
        }
      }
    }
    return getEmbedTextMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static TextServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TextServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TextServiceStub>() {
        @java.lang.Override
        public TextServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TextServiceStub(channel, callOptions);
        }
      };
    return TextServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TextServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TextServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TextServiceBlockingStub>() {
        @java.lang.Override
        public TextServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TextServiceBlockingStub(channel, callOptions);
        }
      };
    return TextServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static TextServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TextServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TextServiceFutureStub>() {
        @java.lang.Override
        public TextServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TextServiceFutureStub(channel, callOptions);
        }
      };
    return TextServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * API for using Generative Language Models (GLMs) trained to generate text.
   * Also known as Large Language Models (LLM)s, these generate text given an
   * input prompt from the user.
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Generates a response from the model given an input message.
     * </pre>
     */
    default void generateText(com.google.ai.generativelanguage.v1beta2.GenerateTextRequest request,
        io.grpc.stub.StreamObserver<com.google.ai.generativelanguage.v1beta2.GenerateTextResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGenerateTextMethod(), responseObserver);
    }

    /**
     * <pre>
     * Generates an embedding from the model given an input message.
     * </pre>
     */
    default void embedText(com.google.ai.generativelanguage.v1beta2.EmbedTextRequest request,
        io.grpc.stub.StreamObserver<com.google.ai.generativelanguage.v1beta2.EmbedTextResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getEmbedTextMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service TextService.
   * <pre>
   * API for using Generative Language Models (GLMs) trained to generate text.
   * Also known as Large Language Models (LLM)s, these generate text given an
   * input prompt from the user.
   * </pre>
   */
  public static abstract class TextServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return TextServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service TextService.
   * <pre>
   * API for using Generative Language Models (GLMs) trained to generate text.
   * Also known as Large Language Models (LLM)s, these generate text given an
   * input prompt from the user.
   * </pre>
   */
  public static final class TextServiceStub
      extends io.grpc.stub.AbstractAsyncStub<TextServiceStub> {
    private TextServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TextServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TextServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Generates a response from the model given an input message.
     * </pre>
     */
    public void generateText(com.google.ai.generativelanguage.v1beta2.GenerateTextRequest request,
        io.grpc.stub.StreamObserver<com.google.ai.generativelanguage.v1beta2.GenerateTextResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGenerateTextMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Generates an embedding from the model given an input message.
     * </pre>
     */
    public void embedText(com.google.ai.generativelanguage.v1beta2.EmbedTextRequest request,
        io.grpc.stub.StreamObserver<com.google.ai.generativelanguage.v1beta2.EmbedTextResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getEmbedTextMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service TextService.
   * <pre>
   * API for using Generative Language Models (GLMs) trained to generate text.
   * Also known as Large Language Models (LLM)s, these generate text given an
   * input prompt from the user.
   * </pre>
   */
  public static final class TextServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<TextServiceBlockingStub> {
    private TextServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TextServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TextServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Generates a response from the model given an input message.
     * </pre>
     */
    public com.google.ai.generativelanguage.v1beta2.GenerateTextResponse generateText(com.google.ai.generativelanguage.v1beta2.GenerateTextRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGenerateTextMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Generates an embedding from the model given an input message.
     * </pre>
     */
    public com.google.ai.generativelanguage.v1beta2.EmbedTextResponse embedText(com.google.ai.generativelanguage.v1beta2.EmbedTextRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getEmbedTextMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service TextService.
   * <pre>
   * API for using Generative Language Models (GLMs) trained to generate text.
   * Also known as Large Language Models (LLM)s, these generate text given an
   * input prompt from the user.
   * </pre>
   */
  public static final class TextServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<TextServiceFutureStub> {
    private TextServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TextServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TextServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Generates a response from the model given an input message.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.ai.generativelanguage.v1beta2.GenerateTextResponse> generateText(
        com.google.ai.generativelanguage.v1beta2.GenerateTextRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGenerateTextMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Generates an embedding from the model given an input message.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.ai.generativelanguage.v1beta2.EmbedTextResponse> embedText(
        com.google.ai.generativelanguage.v1beta2.EmbedTextRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getEmbedTextMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GENERATE_TEXT = 0;
  private static final int METHODID_EMBED_TEXT = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GENERATE_TEXT:
          serviceImpl.generateText((com.google.ai.generativelanguage.v1beta2.GenerateTextRequest) request,
              (io.grpc.stub.StreamObserver<com.google.ai.generativelanguage.v1beta2.GenerateTextResponse>) responseObserver);
          break;
        case METHODID_EMBED_TEXT:
          serviceImpl.embedText((com.google.ai.generativelanguage.v1beta2.EmbedTextRequest) request,
              (io.grpc.stub.StreamObserver<com.google.ai.generativelanguage.v1beta2.EmbedTextResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getGenerateTextMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.google.ai.generativelanguage.v1beta2.GenerateTextRequest,
              com.google.ai.generativelanguage.v1beta2.GenerateTextResponse>(
                service, METHODID_GENERATE_TEXT)))
        .addMethod(
          getEmbedTextMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.google.ai.generativelanguage.v1beta2.EmbedTextRequest,
              com.google.ai.generativelanguage.v1beta2.EmbedTextResponse>(
                service, METHODID_EMBED_TEXT)))
        .build();
  }

  private static abstract class TextServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    TextServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.google.ai.generativelanguage.v1beta2.TextServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("TextService");
    }
  }

  private static final class TextServiceFileDescriptorSupplier
      extends TextServiceBaseDescriptorSupplier {
    TextServiceFileDescriptorSupplier() {}
  }

  private static final class TextServiceMethodDescriptorSupplier
      extends TextServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    TextServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (TextServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new TextServiceFileDescriptorSupplier())
              .addMethod(getGenerateTextMethod())
              .addMethod(getEmbedTextMethod())
              .build();
        }
      }
    }
    return result;
  }
}
