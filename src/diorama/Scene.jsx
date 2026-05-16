import React from 'react';
import { Stars, Environment, SoftShadows } from '@react-three/drei';

export const Scene = ({ options }) => {
  return (
    <>
      <SoftShadows size={15} focus={1.5} samples={10} />
      <ambientLight intensity={0.5} />
      <directionalLight
        castShadow 
        position={[10, 20, 10]}
        intensity={1.5}
        shadow-mapSize={[1024, 1024]}
      >
        <orthographicCamera attach="shadow-camera" args={[-20, 20, 20, -20]} />
      </directionalLight>
      {options.theme === 'dark' && <Stars radius={100} depth={50} count={5000} factor={4} saturation={0} fade speed={1} />}
      <Environment preset="city" />
      <gridHelper args={[100, 50, '#444', '#222']} position={[0, -0.01, 0]} />
    </>
  );
};
