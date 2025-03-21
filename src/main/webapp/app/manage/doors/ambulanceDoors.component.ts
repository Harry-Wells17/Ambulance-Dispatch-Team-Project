import { Component, OnInit } from '@angular/core';
import * as THREE from 'three';
//@ts-ignore
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';
//@ts-ignore
import { Lensflare, LensflareElement } from 'three/addons/objects/Lensflare.js';

@Component({
  selector: 'ambulance-rotate',
  templateUrl: './ambulanceDoors.component.html',
  styleUrls: [],
  standalone: true,
})
export class AmbulanceDoors implements OnInit {
  public scene: THREE.Scene;
  constructor() {}

  ngOnInit(): void {
    this.createThreeJsBox();
  }

  loadAmbukance(scene): Promise<any> {
    return new Promise((resolve, reject) => {
      const loader = new GLTFLoader();

      loader.load('/content/models/SJA.glb', function (gltf) {
        try {
          const textureLoader = new THREE.TextureLoader();
          const textureFlare0 = textureLoader.load('content/images/lensflare0.png');
          let addLenseFlareFunc = offset => {
            const lensflare = new Lensflare();

            lensflare.addElement(new LensflareElement(textureFlare0, 512, 0, new THREE.Color(0x085bce)));

            const newGroup = new THREE.Group();
            newGroup.position.set(offset, 0, 0);
            newGroup.add(lensflare);
            return newGroup;
          };

          scene.add(gltf.scene);

          const lightsF = gltf.scene.children[17].children[0];
          const lightsForF: any[] = [];
          let isFOn = false;

          const lightsB = gltf.scene.children[20].children[0];
          const lightsForB: any[] = [];
          let isBOn = false;

          const lightsL = gltf.scene.children[18].children[0];
          const lightsForL: any[] = [];
          let isLOn = false;

          const lightsR = gltf.scene.children[19].children[0];
          const lightsForR: any[] = [];
          let isROn = false;

          const lightsFR = gltf.scene.children[22];
          const lightsForFR: any[] = [];
          let isFROn = false;

          const lightsFL = gltf.scene.children[21];
          const lightsForFL: any[] = [];
          let isFLOn = false;

          let CreateLight = (x, y, z, offset, arrayIn) => {
            const light = new THREE.PointLight(0x085bce, 100, 100);
            light.position.set(x, y, z);
            light.add(addLenseFlareFunc(offset));
            arrayIn.push(light);
          };

          CreateLight(6, -6, 0, -0.5, lightsForF);
          CreateLight(-6, -6, 0, 0.5, lightsForF);

          const changeF = () => {
            if (isFOn) {
              lightsF.material = new THREE.MeshBasicMaterial({ color: 0xa3a2a5 });
              lightsForF.forEach(lightX => {
                lightsF.remove(lightX);
              });
              isFOn = false;
            } else {
              lightsF.material = new THREE.MeshBasicMaterial({ color: 0x085bce });
              lightsForF.forEach(lightX => {
                lightsF.add(lightX);
              });

              isFOn = true;
            }
          };

          CreateLight(6, 6.5, 0, -0.5, lightsForB);
          CreateLight(-6, 6.5, 0, 0.5, lightsForB);

          const changeB = () => {
            if (isBOn) {
              lightsB.material = new THREE.MeshBasicMaterial({ color: 0xa3a2a5 });
              isBOn = false;

              lightsForB.forEach(lightX => {
                lightsB.remove(lightX);
              });
            } else {
              lightsB.material = new THREE.MeshBasicMaterial({ color: 0x085bce });
              isBOn = true;

              lightsForB.forEach(lightX => {
                lightsB.add(lightX);
              });
            }
          };

          CreateLight(-4.2, 9, -0.5, 0, lightsForL);
          CreateLight(-3.4, 9, -0.5, 0, lightsForL);
          CreateLight(-2.6, 9, -0.5, 0, lightsForL);

          CreateLight(-4.2, -7.5, -0.5, 0, lightsForL);
          CreateLight(-3.4, -7.5, -0.5, 0, lightsForL);
          CreateLight(-2.6, -7.5, -0.5, 0, lightsForL);

          const changeL = () => {
            if (isLOn) {
              lightsL.material = new THREE.MeshBasicMaterial({ color: 0xa3a2a5 });
              lightsForL.forEach(lightX => {
                lightsL.remove(lightX);
              });
              isLOn = false;
            } else {
              lightsL.material = new THREE.MeshBasicMaterial({ color: 0x085bce });
              lightsForL.forEach(lightX => {
                lightsL.add(lightX);
              });

              isLOn = true;
            }
          };

          CreateLight(4.2, 9, -0.5, 0, lightsForR);
          CreateLight(3.4, 9, -0.5, 0, lightsForR);
          CreateLight(2.6, 9, -0.5, 0, lightsForR);

          CreateLight(4.2, -7.5, -0.5, 0, lightsForR);
          CreateLight(3.4, -7.5, -0.5, 0, lightsForR);
          CreateLight(2.6, -7.5, -0.5, 0, lightsForR);

          const changeR = () => {
            if (isROn) {
              lightsR.material = new THREE.MeshBasicMaterial({ color: 0xa3a2a5 });
              lightsForR.forEach(lightX => {
                lightsR.remove(lightX);
              });
              isROn = false;
            } else {
              lightsR.material = new THREE.MeshBasicMaterial({ color: 0x085bce });
              lightsForR.forEach(lightX => {
                lightsR.add(lightX);
              });

              isROn = true;
            }
          };

          CreateLight(1.6, -17, 6, 0, lightsForFR);

          const changeFR = () => {
            if (isFROn) {
              lightsFR.material = new THREE.MeshBasicMaterial({ color: 0xa3a2a5 });
              lightsForFR.forEach(lightX => {
                lightsFR.remove(lightX);
              });
              isFROn = false;
            } else {
              lightsFR.material = new THREE.MeshBasicMaterial({ color: 0x085bce });
              lightsForFR.forEach(lightX => {
                lightsFR.add(lightX);
              });

              isFROn = true;
            }
          };

          CreateLight(-1.6, -17, 6, 0, lightsForFL);

          const changeFL = () => {
            if (isFLOn) {
              lightsFL.material = new THREE.MeshBasicMaterial({ color: 0xa3a2a5 });
              lightsForFL.forEach(lightX => {
                lightsFL.remove(lightX);
              });
              isFLOn = false;
            } else {
              lightsFL.material = new THREE.MeshBasicMaterial({ color: 0x085bce });
              lightsForFL.forEach(lightX => {
                lightsFL.add(lightX);
              });

              isFLOn = true;
            }
          };
          let inerval;

          let startInterva = () => {
            changeF();
            changeR();
            changeFL();
            inerval = setInterval(() => {
              changeF();
              changeR();
              changeFL();
              changeB();
              changeL();
              changeFR();
            }, 150);
          };

          let turnAlloff = () => {
            if (inerval) {
              clearInterval(inerval);
              inerval = undefined;
            }

            if (isFOn) changeF();
            if (isBOn) changeB();
            if (isLOn) changeL();
            if (isROn) changeR();
            if (isFLOn) changeFL();
            if (isFROn) changeFR();
          };

          let flip = true;
          startInterva();

          window.addEventListener('keypress', k => {
            if (k.key === 'a') {
              if (flip) {
                turnAlloff();
                flip = false;
              } else {
                startInterva();
                flip = true;
              }
            }
          });

          const geometry = new THREE.BoxGeometry(1, 1, 1);
          const material = new THREE.MeshBasicMaterial({ color: 0x00ff00 });
          const cube = new THREE.Mesh(geometry, material);
          cube.position.set(1.6, -17, 6);
          //lightsF.add(cube);

          // off: 0xa3a2a5
          // on: 0x085bce

          resolve(gltf.scene);
        } catch (e) {
          console.log(e);
        }
      });
    });
  }

  async createThreeJsBox(): Promise<void> {
    const canvas = document.getElementById('canvas-box');

    this.scene = new THREE.Scene();

    const ambientLight = new THREE.AmbientLight(0xffffff, 1);
    this.scene.add(ambientLight);

    const ambulance = await this.loadAmbukance(this.scene);

    const canvasSizes = {
      width: window.innerWidth,
      height: 800,
    };

    const camera = new THREE.PerspectiveCamera(75, canvasSizes.width / canvasSizes.height, 1, 100);
    camera.position.z = 25;
    camera.lookAt(new THREE.Vector3(0, 0, 0));
    this.scene.add(camera);

    if (!canvas) {
      console.log('Canvas not found');
      return;
    }

    const renderer = new THREE.WebGLRenderer({
      canvas: canvas,
      alpha: true,
    });

    renderer.setClearColor(0x262626, 1);
    renderer.setSize(canvasSizes.width, canvasSizes.height);

    window.addEventListener('resize', () => {
      canvasSizes.width = window.innerWidth;
      canvasSizes.height = 800;

      camera.aspect = canvasSizes.width / canvasSizes.height;
      camera.updateProjectionMatrix();

      renderer.setSize(canvasSizes.width, canvasSizes.height);
      renderer.render(this.scene, camera);
    });

    let doorsL = ambulance.children[2];
    let doorsR = ambulance.children[23];
    let doorsSlide = ambulance.children[1];

    let maxOpenL = Math.PI - 0.5;
    let maxOpenR = -Math.PI + 0.5;

    const animateGeometry = () => {
      let pos = (canvas?.parentElement?.getBoundingClientRect().top ?? 0) + 500;

      if (pos <= 0) {
        if (pos <= -313) pos = -313;
        camera.position.z = 25 + pos * 0.05;

        pos = 0;
      }
      pos = pos * 0.005;

      ambulance.rotation.y = pos;

      let mL = maxOpenL - pos;
      let mR = maxOpenR + pos;

      if (mL < 0) mL = 0;
      if (mR > 0) mR = 0;
      doorsL.rotation.set(Math.PI / 2, 0, mL);
      doorsR.rotation.set(Math.PI / 2, 0, mR);

      let move = 2.8 - pos * 2;
      if (move < -2.8) move = -2.8;
      doorsSlide.position.set(-4.70859, 0, move);

      renderer.render(this.scene, camera);

      window.requestAnimationFrame(animateGeometry);
    };

    animateGeometry();
  }
}
