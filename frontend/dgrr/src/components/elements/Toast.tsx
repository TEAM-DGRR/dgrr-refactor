import Swal from 'sweetalert2';

const Toast = Swal.mixin({
  toast: true,
  position: 'top',
  width: '70%',
  showConfirmButton: false,
  timer: 2000,
});

export default Toast;
