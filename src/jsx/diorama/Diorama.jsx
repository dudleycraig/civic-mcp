import React, { Suspense } from 'react';
import { Canvas } from '@react-three/fiber';
import { OrbitControls, Sky, ContactShadows } from '@react-three/drei';
import { Scene } from './Scene';
import { World } from './World';

export default function Diorama({ data, options }) {
  return (
    <Canvas shadows camera={{ position: [0, 5, 10], fov: 50 }}>
      <color attach="background" args={[options.theme === 'dark' ? '#111' : '#eee']} />
      <Suspense fallback={null}>
        <Scene options={options} />
        <World data={data} />
        <Sky sunPosition={[100, 20, 100]} />
        <ContactShadows opacity={0.5} scale={10} blur={1} far={10} />
      </Suspense>
      <OrbitControls makeDefault />
    </Canvas>
  );
};
