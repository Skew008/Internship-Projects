import React, { useState, useEffect } from 'react';
import ReactFlow, {
  MiniMap,
  Controls,
  Background,
  useNodesState,
  useEdgesState,
} from 'reactflow';
import 'reactflow/dist/style.css';
import roadMapApi from '../../services/roadMapApi'; // Import the roadMapApi service
import Navbar from '../Navbar';
import ChatWithUs from '../ChatBot/ChatWithUs';

const RoadmapFlow = () => {
  const [learningPath, setLearningPath] = useState(null);
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);
  const [dynamicNodes, setDynamicNodes] = useState({}); // Keep track of dynamically generated nodes
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Fetch learning path data from backend
  useEffect(() => {
    const fetchLearningPath = async () => {
      try {
        setLoading(true);
        const response = await roadMapApi.fetchLearningPath();
        setLearningPath(response.data);
        setLoading(false);
      } catch (err) {
        console.error('Error fetching learning path:', err);
        setError('Failed to load your learning path. Please try again later.');
        setLoading(false);
      }
    };

    fetchLearningPath();
  }, []);

  // Generate flow data when learning path changes
  useEffect(() => {
    if (learningPath) {
      const { initialNodes, initialEdges } = generateFlowData(learningPath.topics);
      setNodes(initialNodes);
      setEdges(initialEdges);
    }
  }, [learningPath]);

  const generateFlowData = (topics) => {
    const initialNodes = [];
    const initialEdges = [];

    let y = 0;

    Object.entries(topics).forEach(([topicName, topicData], mainIndex) => {
      const parentId = `main-${mainIndex}`;

      initialNodes.push({
        id: parentId,
        data: { label: topicName },
        position: { x: 0, y },
        style: { background: '#facc15', padding: 10, borderRadius: 8, width: 200 },
      });

      let x = 300;
      Object.entries(topicData.subTopics).forEach(([subTopicName, subTopicData], subIndex) => {
        const nodeId = `${parentId}-child-${subIndex}`;

        initialNodes.push({
          id: nodeId,
          data: {
            label: (
              <div
                className="cursor-pointer"
                onClick={() => createDynamicSubNode(nodeId, subTopicName, subTopicData)}
              >
                <div className="font-semibold">{subTopicName}</div>
                <div className="text-xs">{subTopicData.duration}</div>
                <div className="text-xs italic">
                  {subTopicData.completionStatus === 'completed' ? 'Completed' : 'Not Completed'}
                </div>
              </div>
            ),
          },
          position: { x, y: y + 100 },
          style: {
            background: subTopicData.completionStatus === 'completed' ? '#22c55e' : '#f87171',
            color: 'white',
            padding: 10,
            borderRadius: 8,
            width: 200,
          },
        });

        initialEdges.push({ id: `${parentId}-${nodeId}`, source: parentId, target: nodeId, animated: true });
        x += 250;
      });

      y += 250;
    });

    return { initialNodes, initialEdges };
  };

  const createDynamicSubNode = (parentNodeId, title, subTopicData) => {
    // If the node is already generated, do nothing
    if (dynamicNodes[parentNodeId]) return;

    // Create a new node containing learning resources
    const newNodeId = `${parentNodeId}-dynamic`;

    setDynamicNodes((prev) => ({ ...prev, [parentNodeId]: true }));

    const resourcesContent = (
      <div className="space-y-2">
        <div>{title} Learning Resources:</div>
        <ul className="list-disc ml-4">
          {Object.entries(subTopicData.learningResources).map(([label, url]) => (
            <li key={label}>
              <a href={url} className="text-blue-600 underline" target="_blank" rel="noopener noreferrer">
                {label}
              </a>
            </li>
          ))}
        </ul>
        {/* <button
          onClick={() => markAsCompleted(parentNodeId, title)}
          className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
        >
          Mark as Completed
        </button> */}
      </div>
    );

    // Find the parent node to position the resource node properly
    const parentNode = nodes.find(node => node.id === parentNodeId);
    const position = parentNode ? 
      { x: parentNode.position.x + 50, y: parentNode.position.y + 150 } : 
      { x: 300, y: 300 };

    const newNode = {
      id: newNodeId,
      position: position,
      data: { label: resourcesContent },
      style: { background: '#fef9c3', padding: 10, borderRadius: 8, width: 300 },
    };

    const newEdge = {
      id: `${parentNodeId}-${newNodeId}`,
      source: parentNodeId,
      target: newNodeId,
      animated: true,
    };

    setNodes((prev) => [...prev, newNode]);
    setEdges((prev) => [...prev, newEdge]);
  };

  const markAsCompleted = async (nodeId, subTopicName) => {
    try {
      // Extract the main topic name from the node ID
      const mainIndex = nodeId.split('-')[1];
      const mainNodeId = `main-${mainIndex}`;
      const mainNode = nodes.find(node => node.id === mainNodeId);
      const topicName = mainNode?.data?.label;
      
      if (!topicName) {
        console.error('Could not find topic name for node:', nodeId);
        return;
      }

      // Update the completion status in the backend
      await roadMapApi.updateCompletionStatus(topicName, subTopicName, 'completed');

      // Update the UI
      setNodes((prev) =>
        prev.map((node) =>
          node.id === nodeId
            ? { 
                ...node, 
                style: { ...node.style, background: '#22c55e' },
                data: { 
                  ...node.data, 
                  label: React.cloneElement(
                    node.data.label, 
                    {}, 
                    React.Children.map(node.data.label.props.children, child => {
                      if (child.type === 'div' && child.props.className === 'text-xs italic') {
                        return <div className="text-xs italic">Completed</div>;
                      }
                      return child;
                    })
                  )
                }
              }
            : node
        )
      );

      // Update the learning path state
      setLearningPath(prevPath => {
        const updatedTopics = { ...prevPath.topics };
        updatedTopics[topicName].subTopics[subTopicName].completionStatus = 'completed';
        return { ...prevPath, topics: updatedTopics };
      });
      
    } catch (error) {
      console.error('Error updating completion status:', error);
      alert('Failed to update completion status. Please try again.');
    }
  };

  if (loading) {
    return (
      <div className="h-screen w-full flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="h-screen w-full flex items-center justify-center">
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          <p>{error}</p>
          <button 
            onClick={() => window.location.reload()}
            className="mt-2 bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  if (!learningPath) {
    return (
      <div className="h-screen w-full flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-xl font-bold mb-4">No Learning Path Found</h2>
          <p className="mb-4">You don't have a custom learning path yet.</p>
          <button 
            onClick={() => window.location.href = '/survey'}
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          >
            Take Survey to Create Path
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="h-screen w-full">
      <Navbar isLoggedIn />
      <ReactFlow 
        nodes={nodes} 
        edges={edges} 
        onNodesChange={onNodesChange} 
        onEdgesChange={onEdgesChange} 
        fitView
      >
        <MiniMap />
        <Controls />
        <Background gap={12} size={1} />
      </ReactFlow>
      <ChatWithUs />
    </div>
  );
};

export default RoadmapFlow;