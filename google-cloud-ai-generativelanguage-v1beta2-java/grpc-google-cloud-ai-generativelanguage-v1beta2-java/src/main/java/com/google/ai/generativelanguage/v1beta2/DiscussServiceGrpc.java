package com.google.ai.generativelanguage.v1beta2;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * An API for using Generative Language Models (GLMs) in dialog applications.
 * Also known as large language models (LLMs), this API provides models that
 * are trained for multi-turn dialog.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler",
    comments = "Source: google/ai/generativelanguage/v1beta2/discuss_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class DiscussServiceGrpc {

  private DiscussServiceGrpc() {}

  public static final String SERVICE_NAME = "google.ai.generativelanguage.v1beta2.DiscussService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.google.ai.generativelanguage.v1beta2.GenerateMessageRequest,
      com.google.ai.generativelanguage.v1beta2.GenerateMessageResponse> getGenerateMessageMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GenerateMessage",
      requestType = com.google.ai.generativelanguage.v1beta2.GenerateMessageRequest.class,
      responseType = com.google.ai.generativelanguage.v1beta2.GenerateMessageResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.ai.generativelanguage.v1beta2.GenerateMessageRequest,
      com.google.ai.generativelanguage.v1beta2.GenerateMessageResponse> getGenerateMessageMethod() {
    io.grpc.MethodDescriptor<com.google.ai.generativelanguage.v1beta2.GenerateMessageRequest, com.google.ai.generativelanguage.v1beta2.GenerateMessageResponse> getGenerateMessageMethod;
    if ((getGenerateMessageMethod = DiscussServiceGrpc.getGenerateMessageMethod) == null) {
      synchronized (DiscussServiceGrpc.class) {
        if ((getGenerateMessageMethod = DiscussServiceGrpc.getGenerateMessageMethod) == null) {
          DiscussServiceGrpc.getGenerateMessageMethod = getGenerateMessageMethod =
              io.grpc.MethodDescriptor.<com.google.ai.generativelanguage.v1beta2.GenerateMessageRequest, com.google.ai.generativelanguage.v1beta2.GenerateMessageResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GenerateMessage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.ai.generativelanguage.v1beta2.GenerateMessageRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.ai.generativelanguage.v1beta2.GenerateMessageResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DiscussServiceMethodDescriptorSupplier("GenerateMessage"))
              .build();
        }
      }
    }
    return getGenerateMessageMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.ai.generativelanguage.v1beta2.CountMessageTokensRequest,
      com.google.ai.generativelanguage.v1beta2.CountMessageTokensResponse> getCountMessageTokensMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CountMessageTokens",
      requestType = com.google.ai.generativelanguage.v1beta2.CountMessageTokensRequest.class,
      responseType = com.google.ai.generativelanguage.v1beta2.CountMessageTokensResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.ai.generativelanguage.v1beta2.CountMessageTokensRequest,
      com.google.ai.generativelanguage.v1beta2.CountMessageTokensResponse> getCountMessageTokensMethod() {
    io.grpc.MethodDescriptor<com.google.ai.generativelanguage.v1beta2.CountMessageTokensRequest, com.google.ai.generativelanguage.v1beta2.CountMessageTokensResponse> getCountMessageTokensMethod;
    if ((getCountMessageTokensMethod = DiscussServiceGrpc.getCountMessageTokensMethod) == null) {
      synchronized (DiscussServiceGrpc.class) {
        if ((getCountMessageTokensMethod = DiscussServiceGrpc.getCountMessageTokensMethod) == null) {
          DiscussServiceGrpc.getCountMessageTokensMethod = getCountMessageTokensMethod =
              io.grpc.MethodDescriptor.<com.google.ai.generativelanguage.v1beta2.CountMessageTokensRequest, com.google.ai.generativelanguage.v1beta2.CountMessageTokensResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CountMessageTokens"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.ai.generativelanguage.v1beta2.CountMessageTokensRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.ai.generativelanguage.v1beta2.CountMessageTokensResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DiscussServiceMethodDescriptorSupplier("CountMessageTokens"))
              .build();
        }
      }
    }
    return getCountMessageTokensMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DiscussServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DiscussServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DiscussServiceStub>() {
        @java.lang.Override
        public DiscussServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DiscussServiceStub(channel, callOptions);
        }
      };
    return DiscussServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DiscussServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DiscussServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DiscussServiceBlockingStub>() {
        @java.lang.Override
        public DiscussServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DiscussServiceBlockingStub(channel, callOptions);
        }
      };
    return DiscussServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DiscussServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DiscussServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DiscussServiceFutureStub>() {
        @java.lang.Override
        public DiscussServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DiscussServiceFutureStub(channel, callOptions);
        }
      };
    return DiscussServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * An API for using Generative Language Models (GLMs) in dialog applications.
   * Also known as large language models (LLMs), this API provides models that
   * are trained for multi-turn dialog.
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Generates a response from the model given an input `MessagePrompt`.
     * </pre>
     */
    default void generateMessage(com.google.ai.generativelanguage.v1beta2.GenerateMessageRequest request,
        io.grpc.stub.StreamObserver<com.google.ai.generativelanguage.v1beta2.GenerateMessageResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGenerateMessageMethod(), responseObserver);
    }

    /**
     * <pre>
     * Runs a model's tokenizer on a string and returns the token count.
     * </pre>
     */
    default void countMessageTokens(com.google.ai.generativelanguage.v1beta2.CountMessageTokensRequest request,
        io.grpc.stub.StreamObserver<com.google.ai.generativelanguage.v1beta2.CountMessageTokensResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCountMessageTokensMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service DiscussService.
   * <pre>
   * An API for using Generative Language Models (GLMs) in dialog applications.
   * Also known as large language models (LLMs), this API provides models that
   * are trained for multi-turn dialog.
   * </pre>
   */
  public static abstract class DiscussServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return DiscussServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service DiscussService.
   * <pre>
   * An API for using Generative Language Models (GLMs) in dialog applications.
   * Also known as large language models (LLMs), this API provides models that
   * are trained for multi-turn dialog.
   * </pre>
   */
  public static final class DiscussServiceStub
      extends io.grpc.stub.AbstractAsyncStub<DiscussServiceStub> {
    private DiscussServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DiscussServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DiscussServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Generates a response from the model given an input `MessagePrompt`.
     * </pre>
     */
    public void generateMessage(com.google.ai.generativelanguage.v1beta2.GenerateMessageRequest request,
        io.grpc.stub.StreamObserver<com.google.ai.generativelanguage.v1beta2.GenerateMessageResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGenerateMessageMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Runs a model's tokenizer on a string and returns the token count.
     * </pre>
     */
    public void countMessageTokens(com.google.ai.generativelanguage.v1beta2.CountMessageTokensRequest request,
        io.grpc.stub.StreamObserver<com.google.ai.generativelanguage.v1beta2.CountMessageTokensResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCountMessageTokensMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service DiscussService.
   * <pre>
   * An API for using Generative Language Models (GLMs) in dialog applications.
   * Also known as large language models (LLMs), this API provides models that
   * are trained for multi-turn dialog.
   * </pre>
   */
  public static final class DiscussServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<DiscussServiceBlockingStub> {
    private DiscussServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DiscussServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DiscussServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Generates a response from the model given an input `MessagePrompt`.
     * </pre>
     */
    public com.google.ai.generativelanguage.v1beta2.GenerateMessageResponse generateMessage(com.google.ai.generativelanguage.v1beta2.GenerateMessageRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGenerateMessageMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Runs a model's tokenizer on a string and returns the token count.
     * </pre>
     */
    public com.google.ai.generativelanguage.v1beta2.CountMessageTokensResponse countMessageTokens(com.google.ai.generativelanguage.v1beta2.CountMessageTokensRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCountMessageTokensMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service DiscussService.
   * <pre>
   * An API for using Generative Language Models (GLMs) in dialog applications.
   * Also known as large language models (LLMs), this API provides models that
   * are trained for multi-turn dialog.
   * </pre>
   */
  public static final class DiscussServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<DiscussServiceFutureStub> {
    private DiscussServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DiscussServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DiscussServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Generates a response from the model given an input `MessagePrompt`.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.ai.generativelanguage.v1beta2.GenerateMessageResponse> generateMessage(
        com.google.ai.generativelanguage.v1beta2.GenerateMessageRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGenerateMessageMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Runs a model's tokenizer on a string and returns the token count.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.ai.generativelanguage.v1beta2.CountMessageTokensResponse> countMessageTokens(
        com.google.ai.generativelanguage.v1beta2.CountMessageTokensRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCountMessageTokensMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GENERATE_MESSAGE = 0;
  private static final int METHODID_COUNT_MESSAGE_TOKENS = 1;

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
        case METHODID_GENERATE_MESSAGE:
          serviceImpl.generateMessage((com.google.ai.generativelanguage.v1beta2.GenerateMessageRequest) request,
              (io.grpc.stub.StreamObserver<com.google.ai.generativelanguage.v1beta2.GenerateMessageResponse>) responseObserver);
          break;
        case METHODID_COUNT_MESSAGE_TOKENS:
          serviceImpl.countMessageTokens((com.google.ai.generativelanguage.v1beta2.CountMessageTokensRequest) request,
              (io.grpc.stub.StreamObserver<com.google.ai.generativelanguage.v1beta2.CountMessageTokensResponse>) responseObserver);
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
          getGenerateMessageMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.google.ai.generativelanguage.v1beta2.GenerateMessageRequest,
              com.google.ai.generativelanguage.v1beta2.GenerateMessageResponse>(
                service, METHODID_GENERATE_MESSAGE)))
        .addMethod(
          getCountMessageTokensMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.google.ai.generativelanguage.v1beta2.CountMessageTokensRequest,
              com.google.ai.generativelanguage.v1beta2.CountMessageTokensResponse>(
                service, METHODID_COUNT_MESSAGE_TOKENS)))
        .build();
  }

  private static abstract class DiscussServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DiscussServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.google.ai.generativelanguage.v1beta2.DiscussServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("DiscussService");
    }
  }

  private static final class DiscussServiceFileDescriptorSupplier
      extends DiscussServiceBaseDescriptorSupplier {
    DiscussServiceFileDescriptorSupplier() {}
  }

  private static final class DiscussServiceMethodDescriptorSupplier
      extends DiscussServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    DiscussServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (DiscussServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DiscussServiceFileDescriptorSupplier())
              .addMethod(getGenerateMessageMethod())
              .addMethod(getCountMessageTokensMethod())
              .build();
        }
      }
    }
    return result;
  }
}
