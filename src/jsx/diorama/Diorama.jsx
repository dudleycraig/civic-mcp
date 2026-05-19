import React, { Suspense } from 'react';
import { Canvas } from '@react-three/fiber';
import { OrbitControls, PerspectiveCamera } from '@react-three/drei';

export default function Diorama({ children, options }) {
  return (
    <Canvas 
      shadows 
      dpr={[1, 2]}
      gl={{ antialias: true }}
    >
      <color attach="background" args={[options.theme === 'dark' ? '#111' : '#eee']} />
      <PerspectiveCamera makeDefault position={[0, 5, 10]} fov={50} />
      <ambientLight intensity={0.5} />
      <directionalLight position={[10, 10, 5]} intensity={1} castShadow />

      <Suspense fallback={null}>
        {children}
      </Suspense>

      <OrbitControls makeDefault />
    </Canvas>
  );
}
