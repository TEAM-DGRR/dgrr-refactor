import Swal from 'sweetalert2';

const Toast = Swal.mixin({
  toast: true,
  position: 'top',
  width: '350px',
  showConfirmButton: false,
  timer: 1000,
});

export default Toast;
