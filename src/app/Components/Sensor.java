	package app.Components;
	
	import app.Ports.URISensorInboundPort;
	import app.Ports.URISensorOutBoundPort;
	import app.connectors.ConnectorRegistre;
	import app.connectors.ConnectorSensor;
	
	import java.time.Instant;
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.HashSet;
	import java.util.Map;
	import java.util.Set;
	import java.util.concurrent.TimeUnit;
	
	import AST.BQuery;
	import AST.GQuery;
	import app.GraphicalNetworkInterface;
	import app.Components.Sensor;
	import app.Interfaces.URISensorinCI;
	import app.Interfaces.URISensoroutCI;
	import app.Models.ExecutionState;
	import app.Models.Position;
	import app.Models.ProcessingNode;
	import app.Models.QueryResult;
	import app.Models.RequestContinuation;
	import app.Models.SensorData;
	import fr.sorbonne_u.components.AbstractComponent;
	import fr.sorbonne_u.components.annotations.OfferedInterfaces;
	import fr.sorbonne_u.components.annotations.RequiredInterfaces;
	import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
	import fr.sorbonne_u.components.exceptions.ComponentStartException;
	import fr.sorbonne_u.components.helpers.TracerI;
	import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
	import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
	import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
	import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
	import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
	import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
	import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
	import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;
	import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PImplI;
	import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
	import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;
	import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
	import fr.sorbonne_u.utils.aclocks.ClocksServer;
	import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
	import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
	import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
	
	// -----------------------------------------------------------------------------
	/**
	 * The class <code>URISensor.java</code> implements a component that provides
	 * URI creation services.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * @author	<a>Malek Bouzarkouna, Younes Chetouani & Mohamed Amine Zemali </a>
	 */
	
	@OfferedInterfaces(offered = {RequestingCI.class, SensorNodeP2PCI.class, URISensorinCI.class})
	@RequiredInterfaces(required = {SensorNodeP2PCI.class, RegistrationCI.class, ClocksServerCI.class, URISensoroutCI.class})
	public class Sensor extends AbstractComponent implements SensorNodeP2PImplI {
		
		// ------------------------------------------------------------------------
		// Instance variables
		// ------------------------------------------------------------------------
		
		protected final String URI;
		
		protected final URISensorInboundPort inboundPort ;
		protected URISensorOutBoundPort	outboundPort ;
			
		protected URISensorOutBoundPort	outboundPortNE ;
		protected URISensorOutBoundPort	outboundPortNW ;
		protected URISensorOutBoundPort	outboundPortSE ;
		protected URISensorOutBoundPort	outboundPortSW ;
		protected Set<NodeInfoI> neighbor;
		protected Set<SensorDataI> sensors;
		private Map<URISensorOutBoundPort, NodeInfoI> nodeOutboundPorts;
		private HashSet<String> processedRequests;
		protected NodeInfoI descriptor;
		
		protected String TEST_CLOCK_URI;
		protected ClocksServerOutboundPort outboundPortClock;
		
		protected final String inboundPortRegister;
		
		protected ExecutionState es;
		
		private GraphicalNetworkInterface gui;
	
		// ------------------------------------------------------------------------
		// Constructor
		// ------------------------------------------------------------------------
	
		protected Sensor(GraphicalNetworkInterface gui, NodeInfoI descriptor, String inboundPortRegister, String uriClock, Position guipos, Set<SensorDataI> sensors) throws Exception {
			
			super("Node : "+ descriptor.nodeIdentifier(), 0, 5) ;
			this.gui = gui;
			gui.addGraphicalNode(descriptor.nodeIdentifier(), (int)((Position) (descriptor.nodePosition())).getx(), (int)((Position) (descriptor.nodePosition())).gety());
			this.TEST_CLOCK_URI = uriClock;
			this.outboundPortClock = new ClocksServerOutboundPort(this);
			outboundPortClock.publishPort();
			
			this.descriptor = descriptor;
	
			this.URI = ((BCM4JavaEndPointDescriptorI) descriptor.endPointInfo()).getInboundPortURI();
			this.inboundPort = new URISensorInboundPort(URI,this) ;
			this.inboundPort.publishPort() ;
			
			this.inboundPortRegister  = inboundPortRegister;
			
			this.outboundPort = new URISensorOutBoundPort(this) ; 
			this.outboundPort.publishPort() ;
			
			this.outboundPortNE = new URISensorOutBoundPort(this) ; 
			this.outboundPortNW = new URISensorOutBoundPort(this) ; 
			this.outboundPortSE = new URISensorOutBoundPort(this) ; 
			this.outboundPortSW = new URISensorOutBoundPort(this) ; 
			
			this.nodeOutboundPorts = new HashMap<>();
			
			this.outboundPortNE.publishPort() ;
			this.outboundPortNW.publishPort() ;
			this.outboundPortSE.publishPort() ;
			this.outboundPortSW.publishPort() ;
			
			this.processedRequests = new HashSet<>();
			
			this.sensors = sensors;
			
			TracerI tracer = this.getTracer() ;
			
			tracer.setRelativePosition((int)guipos.getx(), (int)guipos.gety());
		}
	
		// ------------------------------------------------------------------------
		// Component life-cycle
		// ------------------------------------------------------------------------
		
		@Override
		public void	start() throws ComponentStartException{
			
			// ---------------------------------------------------------------------
			// Connection phase
			// ---------------------------------------------------------------------
			
			// do the connection with the clock
			try {
				this.doPortConnection(
						outboundPortClock.getPortURI(),
						ClocksServer.STANDARD_INBOUNDPORT_URI,
						ClocksServerConnector.class.getCanonicalName());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// do the connection with the register
			try {
				this.doPortConnection(
								this.outboundPort.getPortURI(),
								inboundPortRegister,
								ConnectorRegistre.class.getCanonicalName()) ;
					} catch (Exception e) {
						e.printStackTrace();
					}
			
			 StringBuilder logMessageBuilder = new StringBuilder();
		        logMessageBuilder.append("Sensor values").append(this.descriptor.nodeIdentifier()).append(": [");

		        for (SensorDataI sensorData : this.sensors) {
		            logMessageBuilder.append(sensorData.getSensorIdentifier())
		                             .append(": ").append(sensorData.getValue()).append(", ");
		        }
		        if (!this.sensors.isEmpty()) {
		            logMessageBuilder.setLength(logMessageBuilder.length() - 2); // Retire la dernière virgule et l'espace
		        }

		        logMessageBuilder.append("]");
		        this.logMessage(logMessageBuilder.toString());
		        
			super.start() ;
		}
		
		public void	 execute() throws Exception {
			
			this.logMessage("executing node component.") ;
			
			AcceleratedClock ac = outboundPortClock.getClock(TEST_CLOCK_URI);
			ac.waitUntilStart();
			Instant i0 = ac.getStartInstant();
			Instant i1= i0.plusSeconds(60);
			
			long delay = ac.nanoDelayUntilInstant(i1); // délai en nanosecondes
			
			this.scheduleTask(
					o -> { try {
						this.logMessage("registerAndConnectToNeighbor sera exécuté après le délai "+ delay +" nanosecondes.");
		                this.registerAndConnectToNeighbor();
					} catch (Exception e) {
						e.printStackTrace();
					}
					},
					delay, TimeUnit.NANOSECONDS);
			
		    if (this.outboundPortNE.connected()) {
		    	this.outboundPortNE.ask4Disconnection(descriptor);  
		    }
		    if (this.outboundPortNW.connected()) {
		    	this.outboundPortNW.ask4Disconnection(descriptor);  
		    }
		    if (this.outboundPortSE.connected()) {
		    	this.outboundPortSE.ask4Disconnection(descriptor);
		    }   
		    if (this.outboundPortSW.connected()) {
		    	this.outboundPortSW.ask4Disconnection(descriptor); 
		    }
		
		}	
		
		@Override
		public void finalise() throws Exception {
	//		Lorsque le nœud quitte le réseau de capteur, il doit d’abord se déconnecter de ses voisins puis
	//		se désenregistrer auprès du registre en appelant la méthode unregister
		    this.logMessage("stopping node component.");
		    this.printExecutionLogOnFile("node");
		      
		    // Vérifie si le port est connecté avant de tenter de le déconnecter
		    
		    if (this.outboundPortClock.connected()) {
		        this.doPortDisconnection(this.outboundPortClock.getPortURI());
		    }
		    
		    this.doPortDisconnection(this.outboundPortNE.getPortURI()); 
		    this.doPortDisconnection(this.outboundPortNW.getPortURI());
	        this.doPortDisconnection(this.outboundPortSE.getPortURI());
	        this.doPortDisconnection(this.outboundPortSW.getPortURI());
	        
	        if (this.outboundPort.connected()) {
		    	this.outboundPort.unregister(descriptor.nodeIdentifier());
		        this.doPortDisconnection(this.outboundPort.getPortURI());
		    }   
	
		    super.finalise();
		}
	
		@Override
		public void shutdown() throws ComponentShutdownException {
			try {
				   outboundPortClock.unpublishPort();
				} catch (Exception e) {
					e.printStackTrace();
				}
		     try {
				this.inboundPort.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPort.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPortNE.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPortNW.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPortSE.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPortSW.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.shutdown();
		}
		
		@Override
		public void shutdownNow() throws ComponentShutdownException {
			try {
				   outboundPortClock.unpublishPort();
				} catch (Exception e) {
					e.printStackTrace();
				}
		     try {
				this.inboundPort.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPort.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPortNE.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPortNW.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPortSE.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPortSW.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.shutdownNow();
		}
	
		//--------------------------------------------------------------------------
		// Component internal services
		//--------------------------------------------------------------------------
		
		public void	 registerAndConnectToNeighbor() throws Exception {
			this.logMessage("registering node ");
			
			this.neighbor = this.outboundPort.register(descriptor) ;
			
			for (NodeInfoI node : neighbor) {
				
				BCM4JavaEndPointDescriptorI EndPointDescriptorNode = (BCM4JavaEndPointDescriptorI) node.endPointInfo();
				
				String inboundPortSensor = EndPointDescriptorNode.getInboundPortURI();
				Direction d = descriptor.nodePosition().directionFrom(node.nodePosition());
				
			    try {
			        if (d.equals(Direction.NE)) {
			            this.doPortConnection(
			                this.outboundPortNE.getPortURI(),
			                inboundPortSensor,
			                ConnectorSensor.class.getCanonicalName());
			            this.nodeOutboundPorts.put(outboundPortNE, node);
			            this.logMessage("connexion uni directionnel vers un voisin au NE " + node.nodeIdentifier()) ;
			            
			            if (this.outboundPortNE.connected()) {
			            	this.outboundPortNE.ask4Connection(descriptor);
			            }
			            
			        } else if (d.equals(Direction.NW)) {
			            this.doPortConnection(
			                this.outboundPortNW.getPortURI(),
			                inboundPortSensor,
			                ConnectorSensor.class.getCanonicalName());
			            this.nodeOutboundPorts.put(outboundPortNW, node);
			            this.logMessage("connexion unidirectionnel vers un voisin au NW " + node.nodeIdentifier()) ;
			            
			            if (this.outboundPortNW.connected()) {
			            	this.outboundPortNW.ask4Connection(descriptor);
			            }
			            
			        } else if (d.equals(Direction.SE)) {
			            this.doPortConnection(
			                this.outboundPortSE.getPortURI(),
			                inboundPortSensor,
			                ConnectorSensor.class.getCanonicalName());
			            this.nodeOutboundPorts.put(outboundPortSE, node);
			            this.logMessage("connexion unidirectionnel vers un voisin au SE " + node.nodeIdentifier()) ;
			            
			            if (this.outboundPortSE.connected()) {
			            	this.outboundPortSE.ask4Connection(descriptor);
			            }
			            
			        } else if (d.equals(Direction.SW)) {
			            this.doPortConnection(
			                this.outboundPortSW.getPortURI(),
			                inboundPortSensor,
			                ConnectorSensor.class.getCanonicalName());
			            this.nodeOutboundPorts.put(outboundPortNW, node);
			            this.logMessage("connexion unidirectionnel vers un voisin au SW " + node.nodeIdentifier()) ;
			            
			            if (this.outboundPortSW.connected()) {
			            	this.outboundPortSW.ask4Connection(descriptor);
			            }
			        }
			    } catch (Exception e) {
			        e.printStackTrace();
			    }
			}
			
		}
		
		public QueryResultI execute(RequestI request) throws Exception{
			RequestContinuationI clientRequest = new RequestContinuation(request.getQueryCode(),request.clientConnectionInfo(), this.es);
			QueryResultI qr = this.execute((RequestContinuationI)clientRequest);
			return qr;
		}
	
		@Override
		public void ask4Connection(NodeInfoI neighbour) throws Exception {
			gui.addGraphicalConnection(this.descriptor.nodeIdentifier(), neighbour.nodeIdentifier());
			
		    Direction d = this.descriptor.nodePosition().directionFrom(neighbour.nodePosition());
		    this.neighbor.add(neighbour);
		    
		    BCM4JavaEndPointDescriptorI endPointDescriptorNode = (BCM4JavaEndPointDescriptorI) neighbour.endPointInfo();
		    String inboundPortSensor = endPointDescriptorNode.getInboundPortURI();
	
		    try {
		        if (d.equals(Direction.NE)) {
		            if (this.outboundPortNE.connected()) {
		                this.outboundPortNE.ask4Disconnection(descriptor);
		                this.doPortDisconnection(this.outboundPortNE.getPortURI());
		            }
		            this.doPortConnection(
		                this.outboundPortNE.getPortURI(),
		                inboundPortSensor,
		                ConnectorSensor.class.getCanonicalName());
		            this.logMessage("connexion bidirectionnel vers un voisin au NE " + neighbour.nodeIdentifier()) ;
	
		        } else if (d.equals(Direction.NW)) {
		            if (this.outboundPortNW.connected()) {
		                this.outboundPortNW.ask4Disconnection(descriptor);
		                this.doPortDisconnection(this.outboundPortNW.getPortURI());
		            }
		            this.doPortConnection(
		                this.outboundPortNW.getPortURI(),
		                inboundPortSensor,
		                ConnectorSensor.class.getCanonicalName());
		            this.logMessage("connexion bidirectionnel vers un voisin au NW " + neighbour.nodeIdentifier()) ;
	
		        } else if (d.equals(Direction.SE)) {
		            if (this.outboundPortSE.connected()) {
		                this.outboundPortSE.ask4Disconnection(descriptor);
		                this.doPortDisconnection(this.outboundPortSE.getPortURI());
		            }
		            this.doPortConnection(
		                this.outboundPortSE.getPortURI(),
		                inboundPortSensor,
		                ConnectorSensor.class.getCanonicalName());
		            this.logMessage("connexion bidirectionnel vers un voisin au SE " + neighbour.nodeIdentifier()) ;
	
		        } else if (d.equals(Direction.SW)) {
		            if (this.outboundPortSW.connected()) {
		                this.outboundPortSW.ask4Disconnection(descriptor);
		                this.doPortDisconnection(this.outboundPortSW.getPortURI());
		            }
		            this.doPortConnection(
		                this.outboundPortSW.getPortURI(),
		                inboundPortSensor,
		                ConnectorSensor.class.getCanonicalName());
		            this.logMessage("connexion bidirectionnel vers un voisin au SW " + neighbour.nodeIdentifier()) ;
	
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		
		private void handleDisconnectionAndReconnection(URISensorOutBoundPort outboundPort, Direction d) throws Exception {
		    if(outboundPort.connected()) {
		        this.doPortDisconnection(outboundPort.getPortURI());
		    }
		    NodeInfoI node = outboundPort.findNewNeighbour(this.descriptor, d);
		    if (node != null) {
		        BCM4JavaEndPointDescriptorI endPointDescriptorNode = (BCM4JavaEndPointDescriptorI) node.endPointInfo();
		        String inboundPortSensor = endPointDescriptorNode.getInboundPortURI();
		        this.doPortConnection(
		            outboundPort.getPortURI(),
		            inboundPortSensor,
		            ConnectorSensor.class.getCanonicalName());
		        outboundPort.ask4Connection(node);
		    }
		}
	
		@Override
		public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		    Direction d = this.descriptor.nodePosition().directionFrom(neighbour.nodePosition());
		 	gui.removeGraphicalConnection(neighbour.nodeIdentifier(), this.descriptor.nodeIdentifier());
		    try {
		        if (d.equals(Direction.NE)) {
		            handleDisconnectionAndReconnection(this.outboundPortNE, d);
		        } else if (d.equals(Direction.NW)) {
		            handleDisconnectionAndReconnection(this.outboundPortNW, d);
		        } else if (d.equals(Direction.SE)) {
		            handleDisconnectionAndReconnection(this.outboundPortSE, d);
		        } else if (d.equals(Direction.SW)) {
		            handleDisconnectionAndReconnection(this.outboundPortSW, d);
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }       
		}
	
	
		@Override
		public QueryResultI execute(RequestContinuationI request) throws Exception {
			QueryResult queryR = new QueryResult(new ArrayList<>(),false , new ArrayList<>());
			if (this.processedRequests.contains(request.requestURI())) {
	            return queryR;
	        } else {
			ProcessingNode node = new ProcessingNode(this.descriptor.nodeIdentifier(),
					this.descriptor.nodePosition(),this.neighbor,sensors);
			ExecutionState es = new ExecutionState(node,queryR);
			if(request.getQueryCode() instanceof GQuery) {
				((GQuery) request.getQueryCode()).eval(es);
				this.logMessage(es.getCurrentResult().gatheredSensorsValues().toString());
			}else {
				((BQuery) request.getQueryCode()).eval(es);
				this.logMessage(es.getCurrentResult().positiveSensorNodes().toString());
			}
			
			
			gui.toggleNodeBlinking(this.descriptor.nodeIdentifier());
			if(es.isFlooding()) {
				this.processedRequests.add(request.requestURI());
				for(NodeInfoI n : es.getProcessingNode().getNeighbours()) {
					Direction d = es.getProcessingNode().getPosition().directionFrom(n.nodePosition());
					if(es.withinMaximalDistance(n.nodePosition())) {
						try {
					        if (d.equals(Direction.NE)) {
					    		if(outboundPortNE.connected()) {
					    			 es.addToCurrentResult(this.outboundPortNE.execute(request));					    		
					    		}
					        } else if (d.equals(Direction.NW)) {
					        	if(outboundPortNW.connected()) {
					        		es.addToCurrentResult(this.outboundPortNW.execute(request));
					        	}
					        } else if (d.equals(Direction.SE)) {
					            if(outboundPortSE.connected()) {
					        		es.addToCurrentResult(this.outboundPortSE.execute(request));
					        	}
					        } else if (d.equals(Direction.SW)) {
					            if(outboundPortSW.connected()) {					     
					            	es.addToCurrentResult(this.outboundPortSW.execute(request));
					        	}
					        }
					        gui.startGraphicalLightAnimation(this.descriptor.nodeIdentifier(),n.nodeIdentifier());
					    } catch (Exception e) {
					        e.printStackTrace();
					    }
					}else {
						return es.getCurrentResult();
					}
				}
				return es.getCurrentResult();
			}
			if(es.isDirectional()) {
				if(es.noMoreHops()) {
					return es.getCurrentResult();
				}else {
					es.incrementHops();
					ArrayList<Direction> tmp = new ArrayList<>();
					tmp.addAll(es.getDirections());
					if (tmp.isEmpty()){ return es.getCurrentResult();}
					Direction d = tmp.get(0);
					 if (d.equals(Direction.NE)) {
				    		if(outboundPortNE.connected()) {
	//			    			gui.startGraphicalLightAnimation(this.descriptor.nodeIdentifier(),this.nodeOutboundPorts.get(outboundPortNE).nodeIdentifier());
				    			es.addToCurrentResult(this.outboundPortNE.execute(request));			            
				    		}
				        } else if (d.equals(Direction.NW)) {
				        	if(outboundPortNW.connected()) {
	//			        		gui.startGraphicalLightAnimation(this.descriptor.nodeIdentifier(),this.nodeOutboundPorts.get(outboundPortNW).nodeIdentifier());
				        		es.addToCurrentResult(this.outboundPortNW.execute(request));         
				    		}
				        } else if (d.equals(Direction.SE)) {
				        	if(outboundPortSE.connected()) {
	//			        		gui.startGraphicalLightAnimation(this.descriptor.nodeIdentifier(),this.nodeOutboundPorts.get(outboundPortSE).nodeIdentifier());
				        		es.addToCurrentResult(this.outboundPortSE.execute(request));
				        		this.logMessage(es.getCurrentResult().gatheredSensorsValues().toString());
				    		}
				        } else if (d.equals(Direction.SW)) {
				        	if(outboundPortSW.connected()) {
	//			        		gui.startGraphicalLightAnimation(this.descriptor.nodeIdentifier(),this.nodeOutboundPorts.get(outboundPortSW).nodeIdentifier());
				        		es.addToCurrentResult(this.outboundPortSW.execute(request));
				    		}
				        }
					 return es.getCurrentResult();
				}
			}}
			
			 return es.getCurrentResult(); 
		}

		@Override
		public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
			// TODO Auto-generated method stub
			
		}
		// -----------------------------------------------------------------------------
		
	}