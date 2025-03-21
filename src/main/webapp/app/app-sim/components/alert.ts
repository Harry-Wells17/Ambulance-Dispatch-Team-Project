export function CreateAlert({
  topText,
  bottomText,
  buttonNum = 2,
  onClick,
}: {
  topText: string;
  bottomText: string;
  onClick: () => void;
  buttonNum?: number;
}): void {
  const root = document.createElement('div');

  Object.assign(root.style, {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
    position: 'absolute',
    top: '13%',
    left: '29%',
    width: '42%',
    height: '74%',
    backgroundColor: 'rgba(0,0,0,0.5)',
    zIndex: '100000',
  });

  const box = document.createElement('div');

  Object.assign(box.style, {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
    width: '200px',
    height: '100px',
    backgroundColor: '#eee',
    borderRadius: '10px',
    overflow: 'hidden',
  });

  const textHolder = document.createElement('div');
  Object.assign(textHolder.style, {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
    height: '60px',
  });

  const title = document.createElement('h1');
  title.innerText = topText;
  Object.assign(title.style, {
    color: 'black',
  });

  const message = document.createElement('h1');
  message.innerText = bottomText;
  Object.assign(message.style, {
    color: 'black',
    fontSize: '12px',
    textAlign: 'center',
  });

  const buttonHolder = document.createElement('div');
  Object.assign(buttonHolder.style, {
    display: 'flex',
    justifyContent: 'space-between',
    width: '100%',
    flexGrow: '1',
    backgroundColor: 'blue',
  });

  if (buttonNum === 2) {
    const button1 = document.createElement('button');
    button1.innerText = 'Yes';
    Object.assign(button1.style, {
      width: '50%',
      height: '100%',
      color: 'blue',
      borderTop: '1px solid #999',
      backgroundColor: '#eee',
    });

    button1.addEventListener('mouseenter', () => {
      button1.style.backgroundColor = '#ddd';
    });
    button1.addEventListener('mouseleave', () => {
      button1.style.backgroundColor = '#eee';
    });

    button1.onclick = () => {
      onClick();
      root.remove();
    };
    buttonHolder.appendChild(button1);

    const button2 = document.createElement('button');
    button2.innerText = 'Cancel';
    Object.assign(button2.style, {
      width: '50%',
      height: '100%',
      color: 'blue',
      borderTop: '1px solid #999',
      borderLeft: '1px solid #999',
      backgroundColor: '#eee',
    });

    button2.addEventListener('mouseenter', () => {
      button2.style.backgroundColor = '#ddd';
    });
    button2.addEventListener('mouseleave', () => {
      button2.style.backgroundColor = '#eee';
    });

    button2.onclick = () => {
      root.remove();
    };
    buttonHolder.appendChild(button2);
  } else {
    const button1 = document.createElement('button');
    button1.innerText = 'Ok';
    Object.assign(button1.style, {
      width: '100%',
      height: '100%',
      color: 'blue',
      borderTop: '1px solid #999',
      backgroundColor: '#eee',
    });

    button1.addEventListener('mouseenter', () => {
      button1.style.backgroundColor = '#ddd';
    });
    button1.addEventListener('mouseleave', () => {
      button1.style.backgroundColor = '#eee';
    });

    button1.onclick = () => {
      onClick();
      root.remove();
    };
    buttonHolder.appendChild(button1);
  }

  textHolder.appendChild(title);
  textHolder.appendChild(message);
  box.appendChild(textHolder);
  box.appendChild(buttonHolder);
  root.appendChild(box);
  document.getElementById('alert-window')?.appendChild(root);
}
