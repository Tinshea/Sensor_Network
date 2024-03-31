package app.Components;
	
import app.Ports.URISensorInboundPort;
import app.Ports.URISensorInboundPortClient;
import app.Ports.URISensorOutBoundPortToNode;
import app.Ports.URISensorOutBoundPortToRegister;
import app.connectors.ConnectorRegistreNode;
import app.connectors.ConnectorSensorToSensor;
	
	import java.time.Instant;
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.HashSet;
	import java.util.Map;
	import java.util.Set;
	import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import AST.Query.BQuery;
import AST.Query.GQuery;
import app.GraphicalNetworkInterface;
	import app.Components.Sensor;
	import app.Models.Bcm4javaEndPointDescriptor;
	import app.Models.Descriptor;
	import app.Models.ExecutionState;
	import app.Models.ProcessingNode;
	import app.Models.QueryResult;
	import app.Models.RequestContinuation;
	import app.Models.SensorConfig;
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
	
	@OfferedInterfaces(offered = {RequestingCI.class, SensorNodeP2PCI.class})
	@RequiredInterfaces(required = {SensorNodeP2PCI.class, RegistrationCI.class, ClocksServerCI.class})
	public class Sensor extends AbstractComponent implements SensorNodeP2PImplI {
		
		// ------------------------------------------------------------------------
		// Instance variables
		// ------------------------------------------------------------------------

		
		protected final URISensorInboundPort inboundPortSensor ;
		protected final URISensorInboundPortClient inboundPortClient ;
		
		protected URISensorOutBoundPortToRegister	outboundPortRegistre ;
			
		protected URISensorOutBoundPortToNode	outboundPortNE ;
		protected URISensorOutBoundPortToNode	outboundPortNW ;
		protected URISensorOutBoundPortToNode	outboundPortSE ;
		protected URISensorOutBoundPortToNode	outboundPortSW ;
		protected Set<NodeInfoI> neighbor;
		protected Set<SensorDataI> sensors;
		private Map<URISensorOutBoundPortToNode, NodeInfoI> nodeOutboundPorts;
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
	
		protected Sensor(SensorConfig config) throws Exception {
	        super("Node : " + config.getName() , 0, 5);
	        this.gui = config.getGui();
	        this.TEST_CLOCK_URI = config.getUriClock();
	        this.sensors = config.getSensors();

	        this.inboundPortSensor = new URISensorInboundPort(this);
	        this.inboundPortSensor.publishPort();
	        
	        this.inboundPortClient = new URISensorInboundPortClient(this);
	        this.inboundPortClient.publishPort();

	        BCM4JavaEndPointDescriptorI urinodeSensor = new Bcm4javaEndPointDescriptor(inboundPortSensor.getPortURI());
	        BCM4JavaEndPointDescriptorI urinodeclient = new Bcm4javaEndPointDescriptor(inboundPortClient.getPortURI());
	        this.descriptor = new Descriptor("n" + config.getName(), urinodeclient, config.getPosition(), 1.5, urinodeSensor);

	        this.inboundPortRegister = config.getInboundPortRegister();
	        initializeOutboundPorts();
	        
	        this.processedRequests = new HashSet<>();
	        
	        gui.addGraphicalNode("n" + config.getName(), config.getPosition().getx(), config.getPosition().gety());
	        // Ajustement de la position du tracer
	        TracerI tracer = this.getTracer();
	        int index = config.getName() - 1;
	        tracer.setRelativePosition(index % 3, (index / 3) + 1);
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
								this.outboundPortRegistre.getPortURI(),
								inboundPortRegister,
								ConnectorRegistreNode.class.getCanonicalName()) ;
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
		    
		    if (this.outboundPortNE.connected()) {
		        this.doPortDisconnection(this.outboundPortNE.getPortURI());
		    }
		    
		    if (this.outboundPortNW.connected()) {
		        this.doPortDisconnection(this.outboundPortNW.getPortURI());
		    }
		    
		    if (this.outboundPortSE.connected()) {
		        this.doPortDisconnection(this.outboundPortSE.getPortURI());
		    }
		    
		    if (this.outboundPortSW.connected()) {
		        this.doPortDisconnection(this.outboundPortSW.getPortURI());
		    }
		    
	        if (this.outboundPortRegistre.connected()) {
		    	this.outboundPortRegistre.unregister(descriptor.nodeIdentifier());
		        this.doPortDisconnection(this.outboundPortRegistre.getPortURI());
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
				this.inboundPortSensor.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPortRegistre.unpublishPort();
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
				this.inboundPortSensor.unpublishPort();
			} catch (Exception e) {
				e.printStackTrace();
			}
		     try {
				this.outboundPortRegistre.unpublishPort();
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
			
			this.neighbor = this.outboundPortRegistre.register(descriptor) ;
			
			for (NodeInfoI node : neighbor) {
				
				BCM4JavaEndPointDescriptorI EndPointDescriptorNode = (BCM4JavaEndPointDescriptorI) node.p2pEndPointInfo();
				
				String inboundPortSensor = EndPointDescriptorNode.getInboundPortURI();
				Direction d = descriptor.nodePosition().directionFrom(node.nodePosition());
				
			    try {
			        if (d.equals(Direction.NE)) {
			            this.doPortConnection(
			                this.outboundPortNE.getPortURI(),
			                inboundPortSensor,
			                ConnectorSensorToSensor.class.getCanonicalName());
			            this.nodeOutboundPorts.put(outboundPortNE, node);
			            this.logMessage("connexion uni directionnel vers un voisin au NE " + node.nodeIdentifier()) ;
			            
			            if (this.outboundPortNE.connected()) {
			            	this.outboundPortNE.ask4Connection(descriptor);
			            }
			            
			        } else if (d.equals(Direction.NW)) {
			            this.doPortConnection(
			                this.outboundPortNW.getPortURI(),
			                inboundPortSensor,
			                ConnectorSensorToSensor.class.getCanonicalName());
			            this.nodeOutboundPorts.put(outboundPortNW, node);
			            this.logMessage("connexion unidirectionnel vers un voisin au NW " + node.nodeIdentifier()) ;
			            
			            if (this.outboundPortNW.connected()) {
			            	this.outboundPortNW.ask4Connection(descriptor);
			            }
			            
			        } else if (d.equals(Direction.SE)) {
			            this.doPortConnection(
			                this.outboundPortSE.getPortURI(),
			                inboundPortSensor,
			                ConnectorSensorToSensor.class.getCanonicalName());
			            this.nodeOutboundPorts.put(outboundPortSE, node);
			            this.logMessage("connexion unidirectionnel vers un voisin au SE " + node.nodeIdentifier()) ;
			            
			            if (this.outboundPortSE.connected()) {
			            	this.outboundPortSE.ask4Connection(descriptor);
			            }
			            
			        } else if (d.equals(Direction.SW)) {
			            this.doPortConnection(
			                this.outboundPortSW.getPortURI(),
			                inboundPortSensor,
			                ConnectorSensorToSensor.class.getCanonicalName());
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
			ProcessingNode node = new ProcessingNode(this.descriptor.nodeIdentifier(),
                    this.descriptor.nodePosition(),this.neighbor,sensors);
            QueryResult queryR = new QueryResult(new ArrayList<>(),new ArrayList<>());
			 ExecutionState executionState = new ExecutionState(node,queryR);
			 this.es = executionState;
			RequestContinuationI clientRequest = new RequestContinuation(request.getQueryCode(),request.clientConnectionInfo(), this.es);
			QueryResultI qr = this.execute((RequestContinuationI)clientRequest);
			this.logMessage(qr.positiveSensorNodes().toString());
		    JOptionPane.showMessageDialog(null, "oui c'est bien passé :" +qr.isBooleanRequest(), "Information", JOptionPane.INFORMATION_MESSAGE);

			return qr;
		}
	
		@Override
		public void ask4Connection(NodeInfoI neighbour) throws Exception {
			gui.addGraphicalConnection(this.descriptor.nodeIdentifier(), neighbour.nodeIdentifier());
			
		    Direction d = this.descriptor.nodePosition().directionFrom(neighbour.nodePosition());
		    this.neighbor.add(neighbour);
		    
		    BCM4JavaEndPointDescriptorI endPointDescriptorNode = (BCM4JavaEndPointDescriptorI) neighbour.p2pEndPointInfo();
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
		                ConnectorSensorToSensor.class.getCanonicalName());
		            this.logMessage("connexion bidirectionnel vers un voisin au NE " + neighbour.nodeIdentifier()) ;
	
		        } else if (d.equals(Direction.NW)) {
		            if (this.outboundPortNW.connected()) {
		                this.outboundPortNW.ask4Disconnection(descriptor);
		                this.doPortDisconnection(this.outboundPortNW.getPortURI());
		            }
		            this.doPortConnection(
		                this.outboundPortNW.getPortURI(),
		                inboundPortSensor,
		                ConnectorSensorToSensor.class.getCanonicalName());
		            this.logMessage("connexion bidirectionnel vers un voisin au NW " + neighbour.nodeIdentifier()) ;
	
		        } else if (d.equals(Direction.SE)) {
		            if (this.outboundPortSE.connected()) {
		                this.outboundPortSE.ask4Disconnection(descriptor);
		                this.doPortDisconnection(this.outboundPortSE.getPortURI());
		            }
		            this.doPortConnection(
		                this.outboundPortSE.getPortURI(),
		                inboundPortSensor,
		                ConnectorSensorToSensor.class.getCanonicalName());
		            this.logMessage("connexion bidirectionnel vers un voisin au SE " + neighbour.nodeIdentifier()) ;
	
		        } else if (d.equals(Direction.SW)) {
		            if (this.outboundPortSW.connected()) {
		                this.outboundPortSW.ask4Disconnection(descriptor);
		                this.doPortDisconnection(this.outboundPortSW.getPortURI());
		            }
		            this.doPortConnection(
		                this.outboundPortSW.getPortURI(),
		                inboundPortSensor,
		                ConnectorSensorToSensor.class.getCanonicalName());
		            this.logMessage("connexion bidirectionnel vers un voisin au SW " + neighbour.nodeIdentifier()) ;
	
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		
		private void handleDisconnectionAndReconnection(URISensorOutBoundPortToNode outboundPort, Direction d) throws Exception {
		    if(outboundPort.connected()) {
		        this.doPortDisconnection(outboundPort.getPortURI());
		    }
		    NodeInfoI node = outboundPortRegistre.findNewNeighbour(this.descriptor, d);
		    if (node != null) {
		        BCM4JavaEndPointDescriptorI endPointDescriptorNode = (BCM4JavaEndPointDescriptorI) node.p2pEndPointInfo();
		        String inboundPortSensor = endPointDescriptorNode.getInboundPortURI();
		        this.doPortConnection(
		            outboundPort.getPortURI(),
		            inboundPortSensor,
		            ConnectorSensorToSensor.class.getCanonicalName());
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
		
		private void initializeOutboundPorts() throws Exception {
	        this.outboundPortRegistre = new URISensorOutBoundPortToRegister(this);
	        this.outboundPortNE = new URISensorOutBoundPortToNode(this);
	        this.outboundPortNW = new URISensorOutBoundPortToNode(this);
	        this.outboundPortSE = new URISensorOutBoundPortToNode(this);
	        this.outboundPortSW = new URISensorOutBoundPortToNode(this);

	        this.nodeOutboundPorts = new HashMap<>();
	        
	        this.outboundPortClock = new ClocksServerOutboundPort(this);
	  			

	        // Publication des ports sortants
	        this.outboundPortRegistre.publishPort();
	        this.outboundPortNE.publishPort();
	        this.outboundPortNW.publishPort();
	        this.outboundPortSE.publishPort();
	        this.outboundPortSW.publishPort();
	        
	        this.outboundPortClock.publishPort();
	    }

	
	
		@Override
		public QueryResultI execute(RequestContinuationI request) throws Exception {
	            if (this.processedRequests.contains(request.requestURI())) {
	                return  new QueryResult(new ArrayList<>(),new ArrayList<>());
	            } else {
	            ProcessingNode node = new ProcessingNode(this.descriptor.nodeIdentifier(),
	                    this.descriptor.nodePosition(),this.neighbor,sensors);
	            ExecutionState es = null;
	            try {
	                es = ((ExecutionState) request.getExecutionState()).clone();
	            } catch (CloneNotSupportedException e) {
	                e.printStackTrace();
	                // Gérer l'exception, par exemple en initialisant `es` avec une nouvelle instance par défaut ou en lançant une exception runtime.
	            }
	            es.updateProcessingNode(node);
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
					    			 this.outboundPortNE.execute(request);					    		
					    		}
					        } else if (d.equals(Direction.NW)) {
					        	if(outboundPortNW.connected()) {
					        		this.outboundPortNW.execute(request);
					        	}
					        } else if (d.equals(Direction.SE)) {
					            if(outboundPortSE.connected()) {
					        		this.outboundPortSE.execute(request);
					        	}
					        } else if (d.equals(Direction.SW)) {
					            if(outboundPortSW.connected()) {					     
					            	this.outboundPortSW.execute(request);
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
					ArrayList<Direction> tmp = new ArrayList<>();
					tmp.addAll(es.getDirections());
					if (tmp.isEmpty()){ return es.getCurrentResult();}
					Direction d = tmp.get(0); // pas le comportement attendu
					 if (d.equals(Direction.NE)) {
				    		if(outboundPortNE.connected()) {
	//			    			gui.startGraphicalLightAnimation(this.descriptor.nodeIdentifier(),this.nodeOutboundPorts.get(outboundPortNE).nodeIdentifier());
				    			this.outboundPortNE.execute(request);			            
				    		}
				        } else if (d.equals(Direction.NW)) {
				        	if(outboundPortNW.connected()) {
	//			        		gui.startGraphicalLightAnimation(this.descriptor.nodeIdentifier(),this.nodeOutboundPorts.get(outboundPortNW).nodeIdentifier());
				        		this.outboundPortNW.execute(request);         
				    		}
				        } else if (d.equals(Direction.SE)) {
				        	if(outboundPortSE.connected()) {
	//			        		gui.startGraphicalLightAnimation(this.descriptor.nodeIdentifier(),this.nodeOutboundPorts.get(outboundPortSE).nodeIdentifier());
				        		this.outboundPortSE.execute(request);
				        		es.getCurrentResult().gatheredSensorsValues().toString();
				    		}
				        } else if (d.equals(Direction.SW)) {
				        	if(outboundPortSW.connected()) {
	//			        		gui.startGraphicalLightAnimation(this.descriptor.nodeIdentifier(),this.nodeOutboundPorts.get(outboundPortSW).nodeIdentifier());
				        		this.outboundPortSW.execute(request);
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